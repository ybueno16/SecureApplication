package com.vault.domain.model.credential;

import java.util.Arrays;
import java.util.Objects;

public final class EncryptedField {

    private final byte[] ciphertext;
    private final byte[] initializationVector;

    public EncryptedField(byte[] ciphertext, byte[] initializationVector) {
        Objects.requireNonNull(ciphertext, "Ciphertext must not be null");
        Objects.requireNonNull(initializationVector, "IV must not be null");
        if (ciphertext.length == 0) {
            throw new IllegalArgumentException("Ciphertext must not be empty");
        }
        if (initializationVector.length == 0) {
            throw new IllegalArgumentException("IV must not be empty");
        }
        this.ciphertext = ciphertext.clone();
        this.initializationVector = initializationVector.clone();
    }

    public byte[] toCiphertext() { return ciphertext.clone(); }

    public byte[] toInitializationVector() { return initializationVector.clone(); }

    public byte[] toCombined() {
        var combined = new byte[initializationVector.length + ciphertext.length];
        System.arraycopy(initializationVector, 0, combined, 0, initializationVector.length);
        System.arraycopy(ciphertext, 0, combined, initializationVector.length, ciphertext.length);
        return combined;
    }

    public static EncryptedField fromCombined(byte[] combined) {
        Objects.requireNonNull(combined, "Combined data must not be null");
        if (combined.length <= 12) {
            throw new IllegalArgumentException("Combined data too short");
        }
        var iv = Arrays.copyOfRange(combined, 0, 12);
        var ct = Arrays.copyOfRange(combined, 12, combined.length);
        return new EncryptedField(ct, iv);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof EncryptedField that)) return false;
        return Arrays.equals(ciphertext, that.ciphertext)
                && Arrays.equals(initializationVector, that.initializationVector);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(ciphertext) + Arrays.hashCode(initializationVector);
    }

    @Override
    public String toString() {
        return "EncryptedField[REDACTED]";
    }
}
