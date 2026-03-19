package com.vault.infrastructure.persistence;

import com.vault.domain.model.shared.Expiration;
import com.vault.domain.model.user.AccountLock;
import com.vault.domain.model.user.Email;
import com.vault.domain.model.user.FailedLoginAttempts;
import com.vault.domain.model.user.KdfSalt;
import com.vault.domain.model.user.PasswordData;
import com.vault.domain.model.user.PasswordHash;
import com.vault.domain.model.user.User;
import com.vault.domain.model.user.UserId;
import com.vault.domain.model.user.UserIdentity;
import com.vault.domain.model.user.UserSecurity;
import com.vault.domain.repository.UserRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final UserRowMapper rowMapper = new UserRowMapper();

    public JdbcUserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(User user) {
        var sql = "INSERT INTO users (id, email, password_hash, kdf_salt, failed_attempts, locked_until) " +
                  "VALUES (:id, :email, :passwordHash, :kdfSalt, :failedAttempts, :lockedUntil)";
        jdbc.update(sql, toParams(user));
    }

    @Override
    public void update(User user) {
        var sql = "UPDATE users SET password_hash = :passwordHash, failed_attempts = :failedAttempts, " +
                  "locked_until = :lockedUntil WHERE id = :id";
        jdbc.update(sql, toParams(user));
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        var sql = "SELECT * FROM users WHERE email = :email";
        var params = new MapSqlParameterSource("email", email.value());
        return jdbc.query(sql, params, rowMapper).stream().findFirst();
    }

    @Override
    public Optional<User> findById(UserId userId) {
        var sql = "SELECT * FROM users WHERE id = :id";
        var params = new MapSqlParameterSource("id", userId.value());
        return jdbc.query(sql, params, rowMapper).stream().findFirst();
    }

    @Override
    public boolean existsByEmail(Email email) {
        var sql = "SELECT COUNT(*) FROM users WHERE email = :email";
        var params = new MapSqlParameterSource("email", email.value());
        var count = jdbc.queryForObject(sql, params, Integer.class);
        return count != null && count > 0;
    }

    private MapSqlParameterSource toParams(User user) {
        var lockedUntil = user.toLockExpiration().value();
        var lockedTimestamp = lockedUntil.equals(Instant.MIN) ? null : Timestamp.from(lockedUntil);
        return new MapSqlParameterSource()
                .addValue("id", user.toUserId().value())
                .addValue("email", user.toEmail().value())
                .addValue("passwordHash", user.toPasswordHash().value())
                .addValue("kdfSalt", user.toKdfSalt().toBytes())
                .addValue("failedAttempts", user.toFailedLoginAttempts().value())
                .addValue("lockedUntil", lockedTimestamp);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            var userId = new UserId(UUID.fromString(rs.getString("id")));
            var email = new Email(rs.getString("email"));
            var identity = new UserIdentity(userId, email);
            var passwordHash = new PasswordHash(rs.getString("password_hash"));
            var kdfSalt = new KdfSalt(rs.getBytes("kdf_salt"));
            var passwordData = new PasswordData(passwordHash, kdfSalt);
            var failedAttempts = new FailedLoginAttempts(rs.getInt("failed_attempts"));
            var lockedUntilTs = rs.getTimestamp("locked_until");
            var expiration = (lockedUntilTs != null) ? Expiration.of(lockedUntilTs.toInstant()) : Expiration.none();
            var accountLock = new AccountLock(failedAttempts, expiration);
            return new User(identity, new UserSecurity(passwordData, accountLock));
        }
    }
}
