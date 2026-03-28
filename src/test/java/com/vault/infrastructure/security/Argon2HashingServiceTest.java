package com.vault.infrastructure.security;

import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.PasswordHash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Argon2HashingServiceTest {

    private Argon2HashingService hashingService;

    @BeforeEach
    void setUp() {
        hashingService = new Argon2HashingService();
    }

    @Test
    void shouldHashPassword() {
        var password = new MasterPassword("secure-password-123".toCharArray());
        var hash = hashingService.hash(password);
        assertNotNull(hash);
        assertFalse(hash.value().isBlank());
    }

    @Test
    void shouldMatchCorrectPassword() {
        var password = new MasterPassword("secure-password-123".toCharArray());
        var hash = hashingService.hash(password);
        var matchPassword = new MasterPassword("secure-password-123".toCharArray());
        assertTrue(hashingService.matches(matchPassword, hash));
    }

    @Test
    void shouldNotMatchIncorrectPassword() {
        var password = new MasterPassword("secure-password-123".toCharArray());
        var hash = hashingService.hash(password);
        var wrongPassword = new MasterPassword("wrong-password-1234".toCharArray());
        assertFalse(hashingService.matches(wrongPassword, hash));
    }

    @Test
    void shouldProduceUniqueHashesForSamePassword() {
        var p1 = new MasterPassword("secure-password-123".toCharArray());
        var p2 = new MasterPassword("secure-password-123".toCharArray());
        var hash1 = hashingService.hash(p1);
        var hash2 = hashingService.hash(p2);
        assertNotEquals(hash1.value(), hash2.value());
    }

    @Test
    void shouldProduceArgon2idHash() {
        var password = new MasterPassword("secure-password-123".toCharArray());
        var hash = hashingService.hash(password);
        assertTrue(hash.value().startsWith("$argon2id$"));
    }

    @Test
    void shouldHandleLongPassword() {
        var longPassword = "a".repeat(128);
        var password = new MasterPassword(longPassword.toCharArray());
        var hash = hashingService.hash(password);
        var matchPassword = new MasterPassword(longPassword.toCharArray());
        assertTrue(hashingService.matches(matchPassword, hash));
    }
}
