package com.vault.domain.model.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenHashTest {

    @Test
    void shouldCreateWithValidHash() {
        var hash = new TokenHash("abc123def");
        assertEquals("abc123def", hash.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new TokenHash(null));
    }

    @Test
    void shouldRejectEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new TokenHash(""));
    }

    @Test
    void shouldRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> new TokenHash("   "));
    }

    @Test
    void shouldHaveValueEquality() {
        assertEquals(new TokenHash("hash"), new TokenHash("hash"));
    }

    @Test
    void shouldNotEqualDifferentHash() {
        assertNotEquals(new TokenHash("hash1"), new TokenHash("hash2"));
    }
}
