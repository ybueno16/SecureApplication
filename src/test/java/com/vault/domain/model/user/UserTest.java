package com.vault.domain.model.user;

import com.vault.domain.model.shared.Expiration;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserWithFactory() {
        var userId = UserId.generate();
        var email = new Email("test@example.com");
        var hash = new PasswordHash("hashed");
        var salt = new KdfSalt(new byte[32]);

        var user = User.create(userId, email, hash, salt);

        assertEquals(userId, user.toUserId());
        assertEquals(email, user.toEmail());
        assertEquals(hash, user.toPasswordHash());
        assertEquals(salt, user.toKdfSalt());
        assertFalse(user.isLocked());
        assertEquals(0, user.toFailedLoginAttempts().value());
    }

    @Test
    void shouldRecordFailedLogin() {
        var user = createTestUser();
        user.recordFailedLogin();
        assertEquals(1, user.toFailedLoginAttempts().value());
    }

    @Test
    void shouldLockAfterTenFailedAttempts() {
        var user = createTestUser();
        for (int i = 0; i < 10; i++) {
            user.recordFailedLogin();
        }
        assertTrue(user.isLocked());
    }

    @Test
    void shouldNotLockBeforeTenFailedAttempts() {
        var user = createTestUser();
        for (int i = 0; i < 9; i++) {
            user.recordFailedLogin();
        }
        assertFalse(user.isLocked());
    }

    @Test
    void shouldResetFailedAttempts() {
        var user = createTestUser();
        user.recordFailedLogin();
        user.recordFailedLogin();
        user.resetFailedAttempts();
        assertEquals(0, user.toFailedLoginAttempts().value());
        assertFalse(user.isLocked());
    }

    @Test
    void shouldMatchOwnUserId() {
        var userId = UserId.generate();
        var user = User.create(userId, new Email("a@b.com"), new PasswordHash("h"), new KdfSalt(new byte[32]));
        assertTrue(user.matchesId(userId));
    }

    @Test
    void shouldNotMatchDifferentUserId() {
        var user = createTestUser();
        assertFalse(user.matchesId(UserId.generate()));
    }

    @Test
    void shouldRejectNullIdentity() {
        assertThrows(NullPointerException.class, () -> new User(null, null));
    }

    @Test
    void shouldExposeAllAccessors() {
        var user = createTestUser();
        assertNotNull(user.toUserId());
        assertNotNull(user.toEmail());
        assertNotNull(user.toPasswordHash());
        assertNotNull(user.toKdfSalt());
        assertNotNull(user.toFailedLoginAttempts());
        assertNotNull(user.toLockExpiration());
    }

    @Test
    void shouldAccumulateFailedAttempts() {
        var user = createTestUser();
        for (int i = 1; i <= 5; i++) {
            user.recordFailedLogin();
            assertEquals(i, user.toFailedLoginAttempts().value());
        }
    }

    private User createTestUser() {
        return User.create(
                UserId.generate(),
                new Email("test@example.com"),
                new PasswordHash("hashed-value"),
                new KdfSalt(new byte[32]));
    }
}
