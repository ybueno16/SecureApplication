package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TimestampsTest {

    @Test
    void shouldCreateWithNow() {
        var before = Instant.now();
        var timestamps = Timestamps.now();
        var after = Instant.now();

        assertFalse(timestamps.createdAt().isBefore(before));
        assertFalse(timestamps.createdAt().isAfter(after));
        assertEquals(timestamps.createdAt(), timestamps.updatedAt());
    }

    @Test
    void shouldUpdateTimestamp() {
        var timestamps = Timestamps.now();
        var original = timestamps.createdAt();

        var updated = timestamps.withUpdatedNow();
        assertEquals(original, updated.createdAt());
        assertFalse(updated.updatedAt().isBefore(original));
    }

    @Test
    void shouldPreserveCreatedAtOnUpdate() {
        var fixed = Instant.parse("2024-01-01T00:00:00Z");
        var timestamps = new Timestamps(fixed, fixed);
        var updated = timestamps.withUpdatedNow();

        assertEquals(fixed, updated.createdAt());
        assertTrue(updated.updatedAt().isAfter(fixed));
    }

    @Test
    void shouldHaveValueEquality() {
        var instant = Instant.parse("2024-06-01T12:00:00Z");
        assertEquals(new Timestamps(instant, instant), new Timestamps(instant, instant));
    }
}
