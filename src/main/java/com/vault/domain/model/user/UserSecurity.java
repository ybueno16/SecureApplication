package com.vault.domain.model.user;

public record UserSecurity(PasswordData passwordData, AccountLock accountLock) {

    public boolean isLocked() {
        return accountLock.isLocked();
    }

    public UserSecurity withFailedAttempt() {
        return new UserSecurity(passwordData, accountLock.withFailedAttempt());
    }

    public UserSecurity withResetAttempts() {
        return new UserSecurity(passwordData, accountLock.withResetAttempts());
    }

    public PasswordHash toPasswordHash() {
        return passwordData.passwordHash();
    }

    public KdfSalt toKdfSalt() {
        return passwordData.kdfSalt();
    }

    public FailedLoginAttempts toFailedLoginAttempts() {
        return accountLock.failedAttempts();
    }

    public com.vault.domain.model.shared.Expiration toLockExpiration() {
        return accountLock.lockedUntil();
    }
}
