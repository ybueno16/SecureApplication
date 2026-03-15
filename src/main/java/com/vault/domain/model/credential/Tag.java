package com.vault.domain.model.credential;

import java.util.Objects;

public record Tag(String value) {

    private static final int MAX_LENGTH = 50;

    public Tag {
        Objects.requireNonNull(value, "Tag must not be null");
        value = value.trim().toLowerCase();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Tag must not be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Tag must not exceed " + MAX_LENGTH + " characters");
        }
    }

    public boolean startsWith(String prefix) {
        return value.startsWith(prefix.toLowerCase());
    }
}
