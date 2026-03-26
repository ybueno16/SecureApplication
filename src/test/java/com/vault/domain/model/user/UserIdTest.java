package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void shouldCreateWithValidUuid() {
        var uuid = UUID.randomUUID();
        var userId = new UserId(uuid);
        assertEquals(uuid, userId.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new UserId(null));
    }

    @Test
    void shouldGenerateUniqueIds() {
        var first = UserId.generate();
        var second = UserId.generate();
        assertNotEquals(first, second);
    }

    @Test
    void shouldCreateFromValidString() {
        var uuid = UUID.randomUUID();
        var userId = UserId.fromString(uuid.toString());
        assertEquals(uuid, userId.value());
    }

    @Test
    void shouldRejectInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> UserId.fromString("not-a-uuid"));
    }

    @Test
    void shouldHaveValueEquality() {
        var uuid = UUID.randomUUID();
        assertEquals(new UserId(uuid), new UserId(uuid));
    }

    @Test
    void shouldNotEqualDifferentId() {
        assertNotEquals(UserId.generate(), UserId.generate());
    }

    @Test
    void shouldHaveConsistentHashCode() {
        var uuid = UUID.randomUUID();
        assertEquals(new UserId(uuid).hashCode(), new UserId(uuid).hashCode());
    }
}
