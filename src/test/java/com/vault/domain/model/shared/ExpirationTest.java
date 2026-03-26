package com.vault.domain.model.shared;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ExpirationTest {

    @Test
    void shouldBeExpiredWhenInPast() {
        var expiration = Expiration.of(Instant.now().minusSeconds(60));
        assertTrue(expiration.isExpired());
        assertFalse(expiration.isActive());
    }

    @Test
    void shouldNotBeExpiredWhenInFuture() {
        var expiration = Expiration.of(Instant.now().plusSeconds(3600));
        assertFalse(expiration.isExpired());
        assertTrue(expiration.isActive());
    }

    @Test
    void shouldHandleNoneExpiration() {
        var expiration = Expiration.none();
        assertTrue(expiration.isExpired());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new Expiration(null));
    }

    @Test
    void shouldHaveValueEquality() {
        var instant = Instant.parse("2025-01-01T00:00:00Z");
        assertEquals(Expiration.of(instant), Expiration.of(instant));
    }
}
