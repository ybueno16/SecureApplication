package com.vault.domain.model.token;

import java.util.Objects;

public record TokenHash(String value) {

    public TokenHash {
        Objects.requireNonNull(value, "TokenHash must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("TokenHash must not be empty");
        }
    }
}
