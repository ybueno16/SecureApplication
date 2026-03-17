package com.vault.domain.model.audit;

public record AuditEntry(AuditActor actor, AuditDetail detail) {
}
