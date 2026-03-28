package com.vault.infrastructure.security;

import com.vault.domain.model.credential.EncryptedField;
import com.vault.domain.model.user.KdfSalt;
import com.vault.domain.model.user.MasterPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {

    private CryptoService cryptoService;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoService(1000); // low iterations for test speed
    }

    @Test
    void shouldEncryptAndDecryptSuccessfully() {
        var plaintext = "my-secret-password".getBytes(StandardCharsets.UTF_8);
        var key = deriveTestKey();

        var encrypted = cryptoService.encrypt(plaintext, key);
        var decrypted = cryptoService.decrypt(encrypted, key);

        assertArrayEquals(plaintext, decrypted);
    }

    @Test
    void shouldProduceUniqueIvPerEncryption() {
        var plaintext = "same-text".getBytes(StandardCharsets.UTF_8);
        var key = deriveTestKey();

        var first = cryptoService.encrypt(plaintext, key);
        var second = cryptoService.encrypt(plaintext, key);

        assertNotEquals(first, second, "Each encryption should produce unique output");
    }

    @Test
    void shouldDeriveConsistentKey() {
        var password = new MasterPassword("my-master-password-12chars".toCharArray());
        var salt = generateSalt();

        var key1 = cryptoService.deriveKey(password, salt);
        var key2 = cryptoService.deriveKey(new MasterPassword("my-master-password-12chars".toCharArray()), salt);

        assertArrayEquals(key1, key2);
        assertEquals(32, key1.length);
    }

    @Test
    void shouldProduceDifferentKeysForDifferentPasswords() {
        var salt = generateSalt();
        var key1 = cryptoService.deriveKey(new MasterPassword("password-one-12chars".toCharArray()), salt);
        var key2 = cryptoService.deriveKey(new MasterPassword("password-two-12chars".toCharArray()), salt);

        assertFalse(java.util.Arrays.equals(key1, key2));
    }

    @Test
    void shouldFailDecryptionWithWrongKey() {
        var plaintext = "secret".getBytes(StandardCharsets.UTF_8);
        var key1 = deriveTestKey();
        var encrypted = cryptoService.encrypt(plaintext, key1);

        var wrongKey = new byte[32];
        new SecureRandom().nextBytes(wrongKey);

        assertThrows(IllegalStateException.class, () -> cryptoService.decrypt(encrypted, wrongKey));
    }

    @Test
    void shouldHandleEmptyPlaintext() {
        var key = deriveTestKey();
        var encrypted = cryptoService.encrypt(new byte[0], key);
        var decrypted = cryptoService.decrypt(encrypted, key);
        assertEquals(0, decrypted.length);
    }

    private byte[] deriveTestKey() {
        var password = new MasterPassword("test-master-password".toCharArray());
        return cryptoService.deriveKey(password, generateSalt());
    }

    private KdfSalt generateSalt() {
        var salt = new byte[32];
        new SecureRandom().nextBytes(salt);
        return new KdfSalt(salt);
    }
}
