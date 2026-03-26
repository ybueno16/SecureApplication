package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class KdfSaltTest {

    @Test
    void shouldCreateWithValidSalt() {
        var salt = generateValidSalt();
        var kdfSalt = new KdfSalt(salt);
        assertArrayEquals(salt, kdfSalt.toBytes());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new KdfSalt(null));
    }

    @Test
    void shouldRejectWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> new KdfSalt(new byte[16]));
    }

    @Test
    void shouldRejectTooLong() {
        assertThrows(IllegalArgumentException.class, () -> new KdfSalt(new byte[64]));
    }

    @Test
    void shouldRejectEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new KdfSalt(new byte[0]));
    }

    @Test
    void shouldReturnClonedBytes() {
        var original = generateValidSalt();
        var kdfSalt = new KdfSalt(original);
        var returned = kdfSalt.toBytes();
        assertNotSame(original, returned);
        assertArrayEquals(original, returned);
    }

    @Test
    void shouldBeImmutableOnConstruction() {
        var original = generateValidSalt();
        var kdfSalt = new KdfSalt(original);
        original[0] = (byte) 0xFF;
        assertNotEquals((byte) 0xFF, kdfSalt.toBytes()[0]);
    }

    @Test
    void shouldHaveValueEquality() {
        var bytes = generateValidSalt();
        assertEquals(new KdfSalt(bytes.clone()), new KdfSalt(bytes.clone()));
    }

    @Test
    void shouldNotEqualDifferentSalt() {
        assertNotEquals(new KdfSalt(generateValidSalt()), new KdfSalt(generateValidSalt()));
    }

    private byte[] generateValidSalt() {
        var salt = new byte[32];
        new SecureRandom().nextBytes(salt);
        return salt;
    }
}
