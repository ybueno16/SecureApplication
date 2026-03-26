package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserIdentityTest {

    @Test
    void shouldCreateWithValidValues() {
        var userId = UserId.generate();
        var email = new Email("test@example.com");
        var identity = new UserIdentity(userId, email);
        assertEquals(userId, identity.userId());
        assertEquals(email, identity.email());
    }

    @Test
    void shouldMatchOwnUserId() {
        var userId = UserId.generate();
        var identity = new UserIdentity(userId, new Email("test@example.com"));
        assertTrue(identity.matchesId(userId));
    }

    @Test
    void shouldNotMatchDifferentUserId() {
        var identity = new UserIdentity(UserId.generate(), new Email("test@example.com"));
        assertFalse(identity.matchesId(UserId.generate()));
    }

    @Test
    void shouldHaveValueEquality() {
        var userId = UserId.generate();
        var email = new Email("test@example.com");
        assertEquals(new UserIdentity(userId, email), new UserIdentity(userId, email));
    }
}
