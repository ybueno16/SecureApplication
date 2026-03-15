package com.vault.domain.model.user;

import com.vault.domain.model.shared.Expiration;

import java.time.Instant;

public record AccountLock(FailedLoginAttempts failedAttempts, Expiration lockedUntil) {

    private static final int MAX_FAILED_ATTEMPTS = 10;
    private static final int LOCK_DURATION_MINUTES = 15;

    public static AccountLock unlocked() {
        return new AccountLock(FailedLoginAttempts.zero(), Expiration.none());
    }

    public boolean isLocked() {
        return lockedUntil.isActive();
    }

    public AccountLock withFailedAttempt() {
        var incremented = failedAttempts.increment();
        if (incremented.hasReachedLimit(MAX_FAILED_ATTEMPTS)) {
            return new AccountLock(incremented, lockExpiration());
        }
        return new AccountLock(incremented, lockedUntil);
    }

    public AccountLock withResetAttempts() {
        return unlocked();
    }

    private Expiration lockExpiration() {
        return Expiration.of(Instant.now().plusSeconds(LOCK_DURATION_MINUTES * 60L));
    }
}
