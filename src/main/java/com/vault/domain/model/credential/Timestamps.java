package com.vault.domain.model.credential;

import java.time.Instant;

public record Timestamps(Instant createdAt, Instant updatedAt) {

    public static Timestamps now() {
        var now = Instant.now();
        return new Timestamps(now, now);
    }

    public Timestamps withUpdatedNow() {
        return new Timestamps(createdAt, Instant.now());
    }
}
