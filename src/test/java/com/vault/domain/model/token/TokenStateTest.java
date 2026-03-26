package com.vault.domain.model.token;

import com.vault.domain.model.shared.Expiration;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenStateTest {

    @Test
    void shouldBeUsableWhenNotRevokedAndNotExpired() {
        var state = new TokenState(Expiration.of(Instant.now().plusSeconds(3600)), false);
        assertTrue(state.isUsable());
    }

    @Test
    void shouldNotBeUsableWhenRevoked() {
        var state = new TokenState(Expiration.of(Instant.now().plusSeconds(3600)), true);
        assertFalse(state.isUsable());
    }

    @Test
    void shouldNotBeUsableWhenExpired() {
        var state = new TokenState(Expiration.of(Instant.now().minusSeconds(60)), false);
        assertFalse(state.isUsable());
    }

    @Test
    void shouldNotBeUsableWhenBothRevokedAndExpired() {
        var state = new TokenState(Expiration.of(Instant.now().minusSeconds(60)), true);
        assertFalse(state.isUsable());
    }

    @Test
    void shouldRevokeState() {
        var state = new TokenState(Expiration.of(Instant.now().plusSeconds(3600)), false);
        var revoked = state.revoke();
        assertTrue(revoked.revoked());
        assertFalse(revoked.isUsable());
    }

    @Test
    void shouldPreserveExpirationOnRevoke() {
        var expiration = Expiration.of(Instant.now().plusSeconds(3600));
        var state = new TokenState(expiration, false);
        var revoked = state.revoke();
        assertEquals(expiration, revoked.expiration());
    }
}
