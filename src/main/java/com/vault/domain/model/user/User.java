package com.vault.domain.model.user;

import com.vault.domain.model.shared.Expiration;

import java.util.Objects;

public final class User {

    private final UserIdentity identity;
    private UserSecurity security;

    public User(UserIdentity identity, UserSecurity security) {
        Objects.requireNonNull(identity, "UserIdentity must not be null");
        Objects.requireNonNull(security, "UserSecurity must not be null");
        this.identity = identity;
        this.security = security;
    }

    public static User create(UserId userId, Email email, PasswordHash hash, KdfSalt salt) {
        var identity = new UserIdentity(userId, email);
        var passwordData = new PasswordData(hash, salt);
        var accountLock = AccountLock.unlocked();
        return new User(identity, new UserSecurity(passwordData, accountLock));
    }

    public void recordFailedLogin() {
        this.security = security.withFailedAttempt();
    }

    public boolean isLocked() {
        return security.isLocked();
    }

    public void resetFailedAttempts() {
        this.security = security.withResetAttempts();
    }

    public boolean matchesId(UserId userId) {
        return identity.matchesId(userId);
    }

    public UserId toUserId() { return identity.userId(); }

    public Email toEmail() { return identity.email(); }

    public PasswordHash toPasswordHash() { return security.toPasswordHash(); }

    public KdfSalt toKdfSalt() { return security.toKdfSalt(); }

    public FailedLoginAttempts toFailedLoginAttempts() { return security.toFailedLoginAttempts(); }

    public Expiration toLockExpiration() { return security.toLockExpiration(); }
}
