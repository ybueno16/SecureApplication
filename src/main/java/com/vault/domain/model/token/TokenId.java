package com.vault.domain.model.token;

import java.util.Objects;
import java.util.UUID;

public record TokenId(UUID value) {

    public TokenId {
        Objects.requireNonNull(value, "TokenId must not be null");
    }

    public static TokenId generate() {
        return new TokenId(UUID.randomUUID());
    }

    public static TokenId fromString(String raw) {
        return new TokenId(UUID.fromString(raw));
    }
}
