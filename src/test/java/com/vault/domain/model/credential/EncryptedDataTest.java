package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptedDataTest {

    @Test
    void shouldCreateWithAllFields() {
        var username = createField();
        var password = createField();
        var notes = createField();
        var data = new EncryptedData(username, password, notes);

        assertEquals(username, data.usernameField());
        assertEquals(password, data.passwordField());
        assertEquals(notes, data.notesField());
    }

    @Test
    void shouldAllowNullNotes() {
        var data = new EncryptedData(createField(), createField(), null);
        assertNull(data.notesField());
    }

    @Test
    void shouldRejectNullUsername() {
        assertThrows(NullPointerException.class, () -> new EncryptedData(null, createField(), createField()));
    }

    @Test
    void shouldRejectNullPassword() {
        assertThrows(NullPointerException.class, () -> new EncryptedData(createField(), null, createField()));
    }

    @Test
    void shouldRedactToString() {
        var data = new EncryptedData(createField(), createField(), null);
        assertEquals("EncryptedData[REDACTED]", data.toString());
    }

    private EncryptedField createField() {
        return new EncryptedField(new byte[]{1, 2, 3}, new byte[]{4, 5, 6});
    }
}
