package com.vault.domain.model.audit;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuditLogIdTest {

    @Test
    void shouldCreateWithValidUuid() {
        var uuid = UUID.randomUUID();
        var id = new AuditLogId(uuid);
        assertEquals(uuid, id.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new AuditLogId(null));
    }

    @Test
    void shouldGenerateUniqueIds() {
        assertNotEquals(AuditLogId.generate(), AuditLogId.generate());
    }

    @Test
    void shouldHaveValueEquality() {
        var uuid = UUID.randomUUID();
        assertEquals(new AuditLogId(uuid), new AuditLogId(uuid));
    }
}
