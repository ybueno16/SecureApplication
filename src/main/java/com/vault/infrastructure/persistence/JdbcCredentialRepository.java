package com.vault.infrastructure.persistence;

import com.vault.domain.model.credential.Credential;
import com.vault.domain.model.credential.CredentialData;
import com.vault.domain.model.credential.CredentialId;
import com.vault.domain.model.credential.CredentialIdentity;
import com.vault.domain.model.credential.CredentialMetadata;
import com.vault.domain.model.credential.EncryptedData;
import com.vault.domain.model.credential.EncryptedField;
import com.vault.domain.model.credential.SiteUrl;
import com.vault.domain.model.credential.Tags;
import com.vault.domain.model.credential.Timestamps;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.CredentialRepository;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcCredentialRepository implements CredentialRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final CredentialRowMapper rowMapper = new CredentialRowMapper();

    public JdbcCredentialRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void save(Credential credential) {
        var sql = "INSERT INTO credentials (id, user_id, site_url, username_enc, password_enc, " +
                  "notes_enc, tags, created_at, updated_at) " +
                  "VALUES (:id, :userId, :siteUrl, :usernameEnc, :passwordEnc, :notesEnc, " +
                  ":tags, :createdAt, :updatedAt)";
        jdbc.update(sql, toParams(credential));
    }

    @Override
    public void update(Credential credential) {
        var sql = "UPDATE credentials SET site_url = :siteUrl, username_enc = :usernameEnc, " +
                  "password_enc = :passwordEnc, notes_enc = :notesEnc, tags = :tags, " +
                  "updated_at = :updatedAt WHERE id = :id";
        jdbc.update(sql, toParams(credential));
    }

    @Override
    public void deleteById(CredentialId credentialId) {
        var sql = "DELETE FROM credentials WHERE id = :id";
        jdbc.update(sql, new MapSqlParameterSource("id", credentialId.value()));
    }

    @Override
    public Optional<Credential> findById(CredentialId credentialId) {
        var sql = "SELECT * FROM credentials WHERE id = :id";
        return jdbc.query(sql, new MapSqlParameterSource("id", credentialId.value()), rowMapper)
                .stream().findFirst();
    }

    @Override
    public List<Credential> findByUserId(UserId userId, String searchTerm, String cursor, int limit) {
        var params = new MapSqlParameterSource("userId", userId.value()).addValue("limit", limit);
        var sql = new StringBuilder("SELECT * FROM credentials WHERE user_id = :userId");

        appendSearchFilter(sql, params, searchTerm);
        appendCursorFilter(sql, params, cursor);
        sql.append(" ORDER BY created_at DESC, id DESC LIMIT :limit");

        return jdbc.query(sql.toString(), params, rowMapper);
    }

    @Override
    public int countByUserId(UserId userId) {
        var sql = "SELECT COUNT(*) FROM credentials WHERE user_id = :userId";
        var count = jdbc.queryForObject(sql, new MapSqlParameterSource("userId", userId.value()), Integer.class);
        return (count != null) ? count : 0;
    }

    private void appendSearchFilter(StringBuilder sql, MapSqlParameterSource params, String term) {
        if (term == null || term.isBlank()) return;
        sql.append(" AND site_url ILIKE :search");
        params.addValue("search", "%" + term + "%");
    }

    private void appendCursorFilter(StringBuilder sql, MapSqlParameterSource params, String cursor) {
        if (cursor == null || cursor.isBlank()) return;
        var decoded = new String(Base64.getUrlDecoder().decode(cursor));
        var parts = decoded.split(":", 2);
        if (parts.length < 2) return;
        var cursorTime = new Timestamp(Long.parseLong(parts[0]));
        var cursorId = UUID.fromString(parts[1]);
        sql.append(" AND (created_at < :cursorTime OR (created_at = :cursorTime AND id < :cursorId))");
        params.addValue("cursorTime", cursorTime).addValue("cursorId", cursorId);
    }

    private MapSqlParameterSource toParams(Credential credential) {
        return new MapSqlParameterSource()
                .addValue("id", credential.toCredentialId().value())
                .addValue("userId", credential.toUserId().value())
                .addValue("siteUrl", credential.toSiteUrl().value())
                .addValue("usernameEnc", credential.encryptedUsername().toCombined())
                .addValue("passwordEnc", credential.encryptedPassword().toCombined())
                .addValue("notesEnc", toNoteBytes(credential))
                .addValue("tags", credential.toTags().toCommaSeparated())
                .addValue("createdAt", Timestamp.from(credential.toTimestamps().createdAt()))
                .addValue("updatedAt", Timestamp.from(credential.toTimestamps().updatedAt()));
    }

    private byte[] toNoteBytes(Credential credential) {
        var notes = credential.encryptedNotes();
        return (notes != null) ? notes.toCombined() : null;
    }

    private static class CredentialRowMapper implements RowMapper<Credential> {
        @Override
        public Credential mapRow(ResultSet rs, int rowNum) throws SQLException {
            var credId = new CredentialId(UUID.fromString(rs.getString("id")));
            var userId = new UserId(UUID.fromString(rs.getString("user_id")));
            var identity = new CredentialIdentity(credId, userId);

            var encUsername = EncryptedField.fromCombined(rs.getBytes("username_enc"));
            var encPassword = EncryptedField.fromCombined(rs.getBytes("password_enc"));
            var notesBytes = rs.getBytes("notes_enc");
            var encNotes = (notesBytes != null) ? EncryptedField.fromCombined(notesBytes) : null;
            var encryptedData = new EncryptedData(encUsername, encPassword, encNotes);

            var siteUrl = new SiteUrl(rs.getString("site_url"));
            var tags = Tags.fromCommaSeparated(rs.getString("tags"));
            var timestamps = new Timestamps(
                    rs.getTimestamp("created_at").toInstant(),
                    rs.getTimestamp("updated_at").toInstant());
            var metadata = new CredentialMetadata(siteUrl, tags, timestamps);

            return new Credential(identity, new CredentialData(encryptedData, metadata));
        }
    }
}
