package com.vault.domain.model.token;

import com.vault.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenIdentityTest {

    @Test
    void shouldExposeAllFields() {
        var tokenId = TokenId.generate();
        var userId = UserId.generate();
        var hash = new TokenHash("hash-value");
        var identity = new TokenIdentity(tokenId, userId, hash);

        assertEquals(tokenId, identity.tokenId());
        assertEquals(userId, identity.userId());
        assertEquals(hash, identity.tokenHash());
    }

    @Test
    void shouldHaveValueEquality() {
        var tokenId = TokenId.generate();
        var userId = UserId.generate();
        var hash = new TokenHash("hash");
        assertEquals(
                new TokenIdentity(tokenId, userId, hash),
                new TokenIdentity(tokenId, userId, hash));
    }
}
