package com.vault.domain.model.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    @Test
    void shouldCreateWithValidValues() {
        var policy = new PasswordPolicy(24, true, false);
        assertEquals(24, policy.length());
        assertTrue(policy.includeSymbols());
        assertFalse(policy.excludeAmbiguous());
    }

    @Test
    void shouldAcceptMinimumLength() {
        assertDoesNotThrow(() -> new PasswordPolicy(8, false, false));
    }

    @Test
    void shouldAcceptMaximumLength() {
        assertDoesNotThrow(() -> new PasswordPolicy(128, true, true));
    }

    @Test
    void shouldRejectLengthBelowMinimum() {
        assertThrows(IllegalArgumentException.class, () -> new PasswordPolicy(7, false, false));
    }

    @Test
    void shouldRejectLengthAboveMaximum() {
        assertThrows(IllegalArgumentException.class, () -> new PasswordPolicy(129, false, false));
    }

    @Test
    void shouldCreateDefaults() {
        var defaults = PasswordPolicy.defaults();
        assertEquals(24, defaults.length());
        assertTrue(defaults.includeSymbols());
        assertFalse(defaults.excludeAmbiguous());
    }
}
