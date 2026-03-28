package com.vault.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratePasswordRequestTest {

    @Test
    void shouldUseDefaultLength() {
        var request = new GeneratePasswordRequest(null, null, null);
        assertEquals(24, request.effectiveLength());
    }

    @Test
    void shouldUseCustomLength() {
        var request = new GeneratePasswordRequest(32, null, null);
        assertEquals(32, request.effectiveLength());
    }

    @Test
    void shouldUseDefaultSymbols() {
        var request = new GeneratePasswordRequest(null, null, null);
        assertTrue(request.effectiveSymbols());
    }

    @Test
    void shouldUseCustomSymbolsFalse() {
        var request = new GeneratePasswordRequest(null, false, null);
        assertFalse(request.effectiveSymbols());
    }

    @Test
    void shouldUseDefaultAmbiguous() {
        var request = new GeneratePasswordRequest(null, null, null);
        assertFalse(request.effectiveAmbiguous());
    }

    @Test
    void shouldUseCustomAmbiguousTrue() {
        var request = new GeneratePasswordRequest(null, null, true);
        assertTrue(request.effectiveAmbiguous());
    }
}
