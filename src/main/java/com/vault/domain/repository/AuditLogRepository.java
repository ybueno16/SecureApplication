package com.vault.domain.repository;

import com.vault.domain.model.audit.AuditLog;

public interface AuditLogRepository {

    void save(AuditLog auditLog);
}
