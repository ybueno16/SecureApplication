package com.vault.domain.model.user;

import java.util.Objects;

public record PasswordHash(String value) {

    public PasswordHash {
        Objects.requireNonNull(value, "PasswordHash must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("PasswordHash must not be empty");
        }
    }
}
