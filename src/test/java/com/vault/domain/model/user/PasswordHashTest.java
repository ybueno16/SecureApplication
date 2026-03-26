package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordHashTest {

    @Test
    void shouldCreateWithValidHash() {
        var hash = new PasswordHash("$argon2id$v=19$hashed-value");
        assertEquals("$argon2id$v=19$hashed-value", hash.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new PasswordHash(null));
    }

    @Test
    void shouldRejectEmptyString() {
        assertThrows(IllegalArgumentException.class, () -> new PasswordHash(""));
    }

    @Test
    void shouldRejectBlankString() {
        assertThrows(IllegalArgumentException.class, () -> new PasswordHash("   "));
    }

    @Test
    void shouldHaveValueEquality() {
        assertEquals(new PasswordHash("hash1"), new PasswordHash("hash1"));
    }

    @Test
    void shouldNotEqualDifferentHash() {
        assertNotEquals(new PasswordHash("hash1"), new PasswordHash("hash2"));
    }
}
