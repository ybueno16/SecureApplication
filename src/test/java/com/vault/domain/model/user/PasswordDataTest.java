package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordDataTest {

    @Test
    void shouldExposePasswordHash() {
        var hash = new PasswordHash("hashed-value");
        var salt = new KdfSalt(new byte[32]);
        var data = new PasswordData(hash, salt);
        assertEquals(hash, data.passwordHash());
    }

    @Test
    void shouldExposeKdfSalt() {
        var hash = new PasswordHash("hashed-value");
        var salt = new KdfSalt(new byte[32]);
        var data = new PasswordData(hash, salt);
        assertEquals(salt, data.kdfSalt());
    }

    @Test
    void shouldHaveValueEquality() {
        var hash = new PasswordHash("h");
        var salt = new KdfSalt(new byte[32]);
        assertEquals(new PasswordData(hash, salt), new PasswordData(hash, salt));
    }
}
