package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedFieldTest {

    @Test
    void shouldCreateWithValidData() {
        var ct = new byte[]{1, 2, 3};
        var iv = new byte[]{4, 5, 6};
        var field = new EncryptedField(ct, iv);
        assertArrayEquals(ct, field.toCiphertext());
        assertArrayEquals(iv, field.toInitializationVector());
    }

    @Test
    void shouldRejectNullCiphertext() {
        assertThrows(NullPointerException.class, () -> new EncryptedField(null, new byte[]{1}));
    }

    @Test
    void shouldRejectNullIv() {
        assertThrows(NullPointerException.class, () -> new EncryptedField(new byte[]{1}, null));
    }

    @Test
    void shouldRejectEmptyCiphertext() {
        assertThrows(IllegalArgumentException.class, () -> new EncryptedField(new byte[0], new byte[]{1}));
    }

    @Test
    void shouldRejectEmptyIv() {
        assertThrows(IllegalArgumentException.class, () -> new EncryptedField(new byte[]{1}, new byte[0]));
    }

    @Test
    void shouldReturnClonedCiphertext() {
        var ct = new byte[]{1, 2, 3};
        var field = new EncryptedField(ct, new byte[]{4, 5, 6});
        var returned = field.toCiphertext();
        assertNotSame(ct, returned);
        returned[0] = 99;
        assertEquals(1, field.toCiphertext()[0]);
    }

    @Test
    void shouldReturnClonedIv() {
        var iv = new byte[]{4, 5, 6};
        var field = new EncryptedField(new byte[]{1, 2, 3}, iv);
        var returned = field.toInitializationVector();
        assertNotSame(iv, returned);
        returned[0] = 99;
        assertEquals(4, field.toInitializationVector()[0]);
    }

    @Test
    void shouldBeImmutableOnConstruction() {
        var ct = new byte[]{1, 2, 3};
        var iv = new byte[]{4, 5, 6};
        var field = new EncryptedField(ct, iv);
        ct[0] = 99;
        iv[0] = 99;
        assertEquals(1, field.toCiphertext()[0]);
        assertEquals(4, field.toInitializationVector()[0]);
    }

    @Test
    void shouldCombineAndSplit() {
        var ct = new byte[]{1, 2, 3, 4, 5};
        var iv = new byte[12];
        Arrays.fill(iv, (byte) 9);
        var field = new EncryptedField(ct, iv);

        var combined = field.toCombined();
        assertEquals(iv.length + ct.length, combined.length);

        var restored = EncryptedField.fromCombined(combined);
        assertArrayEquals(ct, restored.toCiphertext());
        assertArrayEquals(iv, restored.toInitializationVector());
    }

    @Test
    void shouldRejectNullCombined() {
        assertThrows(NullPointerException.class, () -> EncryptedField.fromCombined(null));
    }

    @Test
    void shouldRejectTooShortCombined() {
        assertThrows(IllegalArgumentException.class, () -> EncryptedField.fromCombined(new byte[12]));
    }

    @Test
    void shouldAcceptMinimumCombinedLength() {
        var combined = new byte[13];
        combined[12] = 1;
        assertDoesNotThrow(() -> EncryptedField.fromCombined(combined));
    }

    @Test
    void shouldHaveValueEquality() {
        var field1 = new EncryptedField(new byte[]{1, 2}, new byte[]{3, 4});
        var field2 = new EncryptedField(new byte[]{1, 2}, new byte[]{3, 4});
        assertEquals(field1, field2);
        assertEquals(field1.hashCode(), field2.hashCode());
    }

    @Test
    void shouldNotEqualDifferentData() {
        var field1 = new EncryptedField(new byte[]{1, 2}, new byte[]{3, 4});
        var field2 = new EncryptedField(new byte[]{5, 6}, new byte[]{3, 4});
        assertNotEquals(field1, field2);
    }
}
