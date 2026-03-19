package com.vault.infrastructure.persistence;

import com.vault.domain.model.shared.Expiration;
import com.vault.domain.model.token.RefreshToken;
import com.vault.domain.model.token.TokenHash;
import com.vault.domain.model.token.TokenId;
import com.vault.domain.model.token.TokenIdentity;
import com.vault.domain.model.token.TokenState;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.RefreshTokenRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcRefreshTokenRepository implements RefreshTokenRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final RefreshTokenRowMapper rowMapper = new RefreshTokenRowMapper();

    public JdbcRefreshTokenRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(RefreshToken token) {
        var sql = "INSERT INTO refresh_tokens (id, user_id, token_hash, expires_at, revoked) " +
                  "VALUES (:id, :userId, :tokenHash, :expiresAt, :revoked)";
        jdbc.update(sql, toParams(token));
    }

    @Override
    public void update(RefreshToken token) {
        var sql = "UPDATE refresh_tokens SET revoked = :revoked WHERE id = :id";
        jdbc.update(sql, toParams(token));
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(TokenHash tokenHash) {
        var sql = "SELECT * FROM refresh_tokens WHERE token_hash = :tokenHash";
        return jdbc.query(sql, new MapSqlParameterSource("tokenHash", tokenHash.value()), rowMapper)
                .stream().findFirst();
    }

    @Override
    public void revokeAllByUserId(UserId userId) {
        var sql = "UPDATE refresh_tokens SET revoked = TRUE WHERE user_id = :userId AND revoked = FALSE";
        jdbc.update(sql, new MapSqlParameterSource("userId", userId.value()));
    }

    private MapSqlParameterSource toParams(RefreshToken token) {
        return new MapSqlParameterSource()
                .addValue("id", token.toTokenId().value())
                .addValue("userId", token.toUserId().value())
                .addValue("tokenHash", token.toTokenHash().value())
                .addValue("expiresAt", Timestamp.from(token.toExpiration().value()))
                .addValue("revoked", token.isRevoked());
    }

    private static class RefreshTokenRowMapper implements RowMapper<RefreshToken> {
        @Override
        public RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            var tokenId = new TokenId(UUID.fromString(rs.getString("id")));
            var userId = new UserId(UUID.fromString(rs.getString("user_id")));
            var tokenHash = new TokenHash(rs.getString("token_hash"));
            var identity = new TokenIdentity(tokenId, userId, tokenHash);
            var expiration = Expiration.of(rs.getTimestamp("expires_at").toInstant());
            var state = new TokenState(expiration, rs.getBoolean("revoked"));
            return new RefreshToken(identity, state);
        }
    }
}
