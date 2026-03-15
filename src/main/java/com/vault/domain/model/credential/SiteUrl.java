package com.vault.domain.model.credential;

import java.util.Objects;

public record SiteUrl(String value) {

    private static final int MAX_LENGTH = 2048;

    public SiteUrl {
        Objects.requireNonNull(value, "SiteUrl must not be null");
        value = value.trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("SiteUrl must not be empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("SiteUrl must not exceed " + MAX_LENGTH + " characters");
        }
    }
}
