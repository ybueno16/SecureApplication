package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MasterPasswordTest {

    @Test
    void shouldCreateWithValidPassword() {
        var password = new MasterPassword("valid-password-123".toCharArray());
        assertNotNull(password.toCharArray());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new MasterPassword(null));
    }

    @Test
    void shouldRejectTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new MasterPassword("short".toCharArray()));
    }

    @Test
    void shouldAcceptExactlyMinLength() {
        assertDoesNotThrow(() -> new MasterPassword("123456789012".toCharArray()));
    }

    @Test
    void shouldRejectElevenCharacters() {
        assertThrows(IllegalArgumentException.class, () -> new MasterPassword("12345678901".toCharArray()));
    }

    @Test
    void shouldReturnClonedCharArray() {
        var original = "valid-password-123".toCharArray();
        var password = new MasterPassword(original);
        var returned = password.toCharArray();
        assertNotSame(original, returned);
        assertArrayEquals(original, returned);
    }

    @Test
    void shouldClearPasswordData() {
        var password = new MasterPassword("valid-password-123".toCharArray());
        password.clear();
        var chars = password.toCharArray();
        for (char c : chars) {
            assertEquals('\0', c);
        }
    }

    @Test
    void shouldBeImmutableOnConstruction() {
        var original = "valid-password-123".toCharArray();
        var password = new MasterPassword(original);
        original[0] = 'X';
        assertNotEquals('X', password.toCharArray()[0]);
    }

    @Test
    void shouldHaveValueEquality() {
        var p1 = new MasterPassword("password-12345".toCharArray());
        var p2 = new MasterPassword("password-12345".toCharArray());
        assertEquals(p1, p2);
    }

    @Test
    void shouldNotEqualDifferentPassword() {
        var p1 = new MasterPassword("password-12345".toCharArray());
        var p2 = new MasterPassword("different-12345".toCharArray());
        assertNotEquals(p1, p2);
    }
}
