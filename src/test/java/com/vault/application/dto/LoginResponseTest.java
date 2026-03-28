package com.vault.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void shouldCreateViaFactory() {
        var response = LoginResponse.of("access-token", "refresh-token", 900);
        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        assertEquals(900, response.expiresInSeconds());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void shouldAlwaysSetBearerTokenType() {
        var response = LoginResponse.of("a", "b", 100);
        assertEquals("Bearer", response.tokenType());
    }
}
