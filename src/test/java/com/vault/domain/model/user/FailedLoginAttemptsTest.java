package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FailedLoginAttemptsTest {

    @Test
    void shouldStartAtZero() {
        var attempts = FailedLoginAttempts.zero();
        assertEquals(0, attempts.value());
    }

    @Test
    void shouldIncrement() {
        var attempts = FailedLoginAttempts.zero().increment();
        assertEquals(1, attempts.value());
    }

    @Test
    void shouldReset() {
        var attempts = FailedLoginAttempts.zero().increment().increment().reset();
        assertEquals(0, attempts.value());
    }

    @Test
    void shouldRejectNegative() {
        assertThrows(IllegalArgumentException.class, () -> new FailedLoginAttempts(-1));
    }

    @Test
    void shouldReachLimit() {
        var attempts = new FailedLoginAttempts(10);
        assertTrue(attempts.hasReachedLimit(10));
    }

    @Test
    void shouldNotReachLimitBelowThreshold() {
        var attempts = new FailedLoginAttempts(9);
        assertFalse(attempts.hasReachedLimit(10));
    }

    @Test
    void shouldBeImmutable() {
        var original = FailedLoginAttempts.zero();
        var incremented = original.increment();
        assertEquals(0, original.value());
        assertEquals(1, incremented.value());
    }
}
