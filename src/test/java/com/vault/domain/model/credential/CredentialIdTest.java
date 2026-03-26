package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CredentialIdTest {

    @Test
    void shouldCreateWithValidUuid() {
        var uuid = UUID.randomUUID();
        var id = new CredentialId(uuid);
        assertEquals(uuid, id.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new CredentialId(null));
    }

    @Test
    void shouldGenerateUniqueIds() {
        assertNotEquals(CredentialId.generate(), CredentialId.generate());
    }

    @Test
    void shouldCreateFromValidString() {
        var uuid = UUID.randomUUID();
        var id = CredentialId.fromString(uuid.toString());
        assertEquals(uuid, id.value());
    }

    @Test
    void shouldRejectInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> CredentialId.fromString("not-a-uuid"));
    }

    @Test
    void shouldHaveValueEquality() {
        var uuid = UUID.randomUUID();
        assertEquals(new CredentialId(uuid), new CredentialId(uuid));
    }

    @Test
    void shouldHaveConsistentHashCode() {
        var uuid = UUID.randomUUID();
        assertEquals(new CredentialId(uuid).hashCode(), new CredentialId(uuid).hashCode());
    }
}
