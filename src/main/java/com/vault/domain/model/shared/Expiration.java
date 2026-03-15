package com.vault.domain.model.shared;

import java.time.Instant;
import java.util.Objects;

public record Expiration(Instant value) {

    public Expiration {
        Objects.requireNonNull(value, "Expiration must not be null");
    }

    public static Expiration of(Instant instant) {
        return new Expiration(instant);
    }

    public static Expiration none() {
        return new Expiration(Instant.MIN);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(value);
    }

    public boolean isActive() {
        return !isExpired();
    }
}
