package com.vault.domain.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateValidEmail() {
        var email = new Email("User@Example.COM");
        assertEquals("user@example.com", email.value());
    }

    @Test
    void shouldTrimWhitespace() {
        var email = new Email("  user@example.com  ");
        assertEquals("user@example.com", email.value());
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "test.user+tag@domain.co.uk", "a@b.com"})
    void shouldAcceptValidEmails(String raw) {
        assertDoesNotThrow(() -> new Email(raw));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "notanemail", "@domain.com", "user@", "user @domain.com"})
    void shouldRejectInvalidEmails(String raw) {
        assertThrows(IllegalArgumentException.class, () -> new Email(raw));
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new Email(null));
    }

    @Test
    void shouldRejectExceedingMaxLength() {
        var longEmail = "a".repeat(250) + "@b.com";
        assertThrows(IllegalArgumentException.class, () -> new Email(longEmail));
    }

    @Test
    void shouldHaveValueEquality() {
        assertEquals(new Email("user@example.com"), new Email("USER@example.com"));
    }
}
