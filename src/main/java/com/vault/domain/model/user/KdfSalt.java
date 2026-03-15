package com.vault.domain.model.user;

import java.util.Arrays;
import java.util.Objects;

public final class KdfSalt {

    private static final int REQUIRED_LENGTH = 32;
    private final byte[] value;

    public KdfSalt(byte[] value) {
        Objects.requireNonNull(value, "KdfSalt must not be null");
        if (value.length != REQUIRED_LENGTH) {
            throw new IllegalArgumentException("KdfSalt must be exactly 32 bytes");
        }
        this.value = value.clone();
    }

    public byte[] toBytes() {
        return value.clone();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof KdfSalt that)) return false;
        return Arrays.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(value);
    }

    @Override
    public String toString() {
        return "KdfSalt[REDACTED]";
    }
}
