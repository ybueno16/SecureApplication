package com.vault.domain.model.audit;

import com.vault.domain.model.credential.CredentialId;

import java.time.Instant;

public record AuditDetail(AuditAction action, CredentialId resourceId, Instant timestamp) {
}
