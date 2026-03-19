package com.vault.infrastructure.persistence;

import com.vault.domain.model.audit.AuditLog;
import com.vault.domain.repository.AuditLogRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class JdbcAuditLogRepository implements AuditLogRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcAuditLogRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Async
    @Override
    public void save(AuditLog auditLog) {
        var sql = "INSERT INTO audit_log (id, user_id, action, resource_id, ip_address, timestamp) " +
                  "VALUES (:id, :userId, :action::audit_action, :resourceId, :ipAddress, :timestamp)";
        jdbc.update(sql, toParams(auditLog));
    }

    private MapSqlParameterSource toParams(AuditLog auditLog) {
        var resourceId = auditLog.toResourceId();
        return new MapSqlParameterSource()
                .addValue("id", auditLog.toAuditLogId().value())
                .addValue("userId", auditLog.toUserId().value())
                .addValue("action", auditLog.toAction().name())
                .addValue("resourceId", (resourceId != null) ? resourceId.value() : null)
                .addValue("ipAddress", auditLog.toIpAddress().value())
                .addValue("timestamp", Timestamp.from(auditLog.toTimestamp()));
    }
}
