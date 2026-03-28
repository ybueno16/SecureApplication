package com.vault.infrastructure.security;

import com.vault.domain.model.user.Email;
import com.vault.domain.model.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        var rsaKeyProvider = new RsaKeyProvider();
        jwtService = new JwtService(rsaKeyProvider, "test-issuer", 15);
    }

    @Test
    void shouldGenerateAndParseToken() {
        var userId = UserId.generate();
        var email = new Email("test@example.com");

        var token = jwtService.generateToken(userId, email);
        assertNotNull(token);

        var claims = jwtService.parseToken(token);
        assertEquals(userId.value().toString(), claims.getSubject());
        assertEquals("test@example.com", claims.get("email", String.class));
        assertEquals("test-issuer", claims.getIssuer());
    }

    @Test
    void shouldExtractUserId() {
        var userId = UserId.generate();
        var token = jwtService.generateToken(userId, new Email("user@test.com"));

        var extracted = jwtService.extractUserId(token);
        assertEquals(userId, extracted);
    }

    @Test
    void shouldRejectInvalidToken() {
        assertThrows(Exception.class, () -> jwtService.parseToken("invalid.jwt.token"));
    }

    @Test
    void shouldRejectTamperedToken() {
        var token = jwtService.generateToken(UserId.generate(), new Email("test@test.com"));
        var tampered = token.substring(0, token.length() - 4) + "XXXX";
        assertThrows(Exception.class, () -> jwtService.parseToken(tampered));
    }

    @Test
    void shouldReturnCorrectExpirationSeconds() {
        assertEquals(900, jwtService.accessTokenExpirationSeconds());
    }
}
