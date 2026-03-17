package com.vault.domain.model.audit;

import com.vault.domain.model.credential.CredentialId;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.UserId;

import java.time.Instant;
import java.util.Objects;

public final class AuditLog {

    private final AuditLogId auditLogId;
    private final AuditEntry entry;

    public AuditLog(AuditLogId auditLogId, AuditEntry entry) {
        Objects.requireNonNull(auditLogId, "AuditLogId must not be null");
        Objects.requireNonNull(entry, "AuditEntry must not be null");
        this.auditLogId = auditLogId;
        this.entry = entry;
    }

    public static AuditLog create(UserId userId, AuditAction action,
                                   CredentialId resourceId, IpAddress ipAddress) {
        var actor = new AuditActor(userId, ipAddress);
        var detail = new AuditDetail(action, resourceId, Instant.now());
        return new AuditLog(AuditLogId.generate(), new AuditEntry(actor, detail));
    }

    public AuditLogId toAuditLogId() { return auditLogId; }
    public UserId toUserId() { return entry.actor().userId(); }
    public IpAddress toIpAddress() { return entry.actor().ipAddress(); }
    public AuditAction toAction() { return entry.detail().action(); }
    public CredentialId toResourceId() { return entry.detail().resourceId(); }
    public Instant toTimestamp() { return entry.detail().timestamp(); }
}
