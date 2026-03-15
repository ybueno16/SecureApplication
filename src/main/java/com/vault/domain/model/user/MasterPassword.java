package com.vault.domain.model.user;

import java.util.Arrays;
import java.util.Objects;

public final class MasterPassword {

    private static final int MIN_LENGTH = 12;
    private final char[] value;

    public MasterPassword(char[] value) {
        Objects.requireNonNull(value, "MasterPassword must not be null");
        if (value.length < MIN_LENGTH) {
            throw new IllegalArgumentException("Master password must be at least " + MIN_LENGTH + " characters");
        }
        this.value = value.clone();
    }

    public char[] toCharArray() {
        return value.clone();
    }

    public void clear() {
        Arrays.fill(value, '\0');
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof MasterPassword that)) return false;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "MasterPassword[REDACTED]";
    }
}
