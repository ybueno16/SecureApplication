package com.vault.domain.model.shared;

public record PasswordPolicy(int length, boolean includeSymbols, boolean excludeAmbiguous) {

    public PasswordPolicy {
        if (length < 8 || length > 128) {
            throw new IllegalArgumentException("Password length must be between 8 and 128");
        }
    }

    public static PasswordPolicy defaults() {
        return new PasswordPolicy(24, true, false);
    }
}
