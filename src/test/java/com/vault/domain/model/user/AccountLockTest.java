package com.vault.domain.model.user;

import com.vault.domain.model.shared.Expiration;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AccountLockTest {

    @Test
    void shouldStartUnlocked() {
        var lock = AccountLock.unlocked();
        assertFalse(lock.isLocked());
        assertEquals(0, lock.failedAttempts().value());
    }

    @Test
    void shouldIncrementFailedAttempts() {
        var lock = AccountLock.unlocked().withFailedAttempt();
        assertEquals(1, lock.failedAttempts().value());
        assertFalse(lock.isLocked());
    }

    @Test
    void shouldLockAfterTenFailedAttempts() {
        var lock = AccountLock.unlocked();
        for (int i = 0; i < 10; i++) {
            lock = lock.withFailedAttempt();
        }
        assertTrue(lock.isLocked());
        assertEquals(10, lock.failedAttempts().value());
    }

    @Test
    void shouldNotLockBeforeTenFailedAttempts() {
        var lock = AccountLock.unlocked();
        for (int i = 0; i < 9; i++) {
            lock = lock.withFailedAttempt();
        }
        assertFalse(lock.isLocked());
    }

    @Test
    void shouldResetAttempts() {
        var lock = AccountLock.unlocked().withFailedAttempt().withFailedAttempt().withResetAttempts();
        assertFalse(lock.isLocked());
        assertEquals(0, lock.failedAttempts().value());
    }

    @Test
    void shouldBeLockedWhenExpirationInFuture() {
        var future = Expiration.of(Instant.now().plusSeconds(900));
        var lock = new AccountLock(new FailedLoginAttempts(10), future);
        assertTrue(lock.isLocked());
    }

    @Test
    void shouldNotBeLockedWhenExpirationInPast() {
        var past = Expiration.of(Instant.now().minusSeconds(1));
        var lock = new AccountLock(new FailedLoginAttempts(10), past);
        assertFalse(lock.isLocked());
    }
}
