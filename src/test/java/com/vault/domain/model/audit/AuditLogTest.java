package com.vault.domain.model.audit;

import com.vault.domain.model.credential.CredentialId;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogTest {

    @Test
    void shouldCreateViaFactory() {
        var userId = UserId.generate();
        var credId = CredentialId.generate();
        var log = AuditLog.create(userId, AuditAction.LOGIN, credId, new IpAddress("192.168.1.1"));

        assertEquals(userId, log.toUserId());
        assertEquals(AuditAction.LOGIN, log.toAction());
        assertEquals(credId, log.toResourceId());
        assertEquals("192.168.1.1", log.toIpAddress().value());
        assertNotNull(log.toAuditLogId());
        assertNotNull(log.toTimestamp());
    }

    @Test
    void shouldAllowNullResourceId() {
        var log = AuditLog.create(UserId.generate(), AuditAction.LOGOUT, null, new IpAddress("10.0.0.1"));
        assertNull(log.toResourceId());
    }

    @Test
    void shouldGenerateUniqueAuditLogId() {
        var log1 = AuditLog.create(UserId.generate(), AuditAction.LOGIN, null, new IpAddress("10.0.0.1"));
        var log2 = AuditLog.create(UserId.generate(), AuditAction.LOGIN, null, new IpAddress("10.0.0.1"));
        assertNotEquals(log1.toAuditLogId(), log2.toAuditLogId());
    }

    @Test
    void shouldExposeAllAuditActions() {
        for (var action : AuditAction.values()) {
            var log = AuditLog.create(UserId.generate(), action, null, new IpAddress("127.0.0.1"));
            assertEquals(action, log.toAction());
        }
    }

    @Test
    void shouldRejectNullAuditLogId() {
        assertThrows(NullPointerException.class, () -> new AuditLog(null, null));
    }
}
