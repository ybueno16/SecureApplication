package com.vault.domain.model.audit;

import java.util.Objects;
import java.util.UUID;

public record AuditLogId(UUID value) {

    public AuditLogId {
        Objects.requireNonNull(value, "AuditLogId must not be null");
    }

    public static AuditLogId generate() {
        return new AuditLogId(UUID.randomUUID());
    }
}
