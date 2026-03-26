package com.vault.domain.model.token;

import com.vault.domain.model.shared.Expiration;
import com.vault.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    void shouldBeUsableWhenNotRevokedAndNotExpired() {
        var token = createValidToken();
        assertTrue(token.isUsable());
    }

    @Test
    void shouldNotBeUsableAfterRevocation() {
        var token = createValidToken();
        token.revoke();
        assertFalse(token.isUsable());
        assertTrue(token.isRevoked());
    }

    @Test
    void shouldNotBeUsableWhenExpired() {
        var token = RefreshToken.create(
                TokenId.generate(),
                UserId.generate(),
                new TokenHash("hash-value"),
                Expiration.of(Instant.now().minusSeconds(60)));
        assertFalse(token.isUsable());
    }

    @Test
    void shouldBelongToCorrectUser() {
        var userId = UserId.generate();
        var token = RefreshToken.create(
                TokenId.generate(), userId,
                new TokenHash("hash"), Expiration.of(Instant.now().plusSeconds(3600)));
        assertTrue(token.belongsTo(userId));
        assertFalse(token.belongsTo(UserId.generate()));
    }

    private RefreshToken createValidToken() {
        return RefreshToken.create(
                TokenId.generate(),
                UserId.generate(),
                new TokenHash("test-hash"),
                Expiration.of(Instant.now().plusSeconds(86400)));
    }
}
