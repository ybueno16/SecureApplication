package com.vault.domain.model.user;

public record FailedLoginAttempts(int value) {

    public FailedLoginAttempts {
        if (value < 0) {
            throw new IllegalArgumentException("FailedLoginAttempts cannot be negative");
        }
    }

    public static FailedLoginAttempts zero() {
        return new FailedLoginAttempts(0);
    }

    public FailedLoginAttempts increment() {
        return new FailedLoginAttempts(value + 1);
    }

    public FailedLoginAttempts reset() {
        return zero();
    }

    public boolean hasReachedLimit(int maxAttempts) {
        return value >= maxAttempts;
    }
}
