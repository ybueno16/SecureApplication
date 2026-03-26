package com.vault.domain.model.user;

import com.vault.domain.model.shared.Expiration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSecurityTest {

    @Test
    void shouldNotBeLockedInitially() {
        var security = createUnlockedSecurity();
        assertFalse(security.isLocked());
    }

    @Test
    void shouldIncrementFailedAttempts() {
        var security = createUnlockedSecurity().withFailedAttempt();
        assertEquals(1, security.toFailedLoginAttempts().value());
    }

    @Test
    void shouldResetAttempts() {
        var security = createUnlockedSecurity().withFailedAttempt().withFailedAttempt().withResetAttempts();
        assertEquals(0, security.toFailedLoginAttempts().value());
    }

    @Test
    void shouldExposePasswordHash() {
        var hash = new PasswordHash("hashed");
        var salt = new KdfSalt(new byte[32]);
        var security = new UserSecurity(new PasswordData(hash, salt), AccountLock.unlocked());
        assertEquals(hash, security.toPasswordHash());
    }

    @Test
    void shouldExposeKdfSalt() {
        var salt = new KdfSalt(new byte[32]);
        var security = new UserSecurity(new PasswordData(new PasswordHash("h"), salt), AccountLock.unlocked());
        assertEquals(salt, security.toKdfSalt());
    }

    @Test
    void shouldExposeLockExpiration() {
        var security = createUnlockedSecurity();
        assertNotNull(security.toLockExpiration());
    }

    private UserSecurity createUnlockedSecurity() {
        var passwordData = new PasswordData(new PasswordHash("h"), new KdfSalt(new byte[32]));
        return new UserSecurity(passwordData, AccountLock.unlocked());
    }
}
