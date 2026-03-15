package com.vault.domain.model.user;

import java.util.Objects;
import java.util.regex.Pattern;

public record Email(String value) {

    private static final int MAX_LENGTH = 255;
    private static final Pattern RFC_5322 = Pattern.compile(
            "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?" +
            "(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");

    public Email {
        Objects.requireNonNull(value, "Email must not be null");
        value = value.trim().toLowerCase();
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Email must not exceed " + MAX_LENGTH + " characters");
        }
        if (!RFC_5322.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}
