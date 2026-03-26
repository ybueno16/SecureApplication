package com.vault.domain.model.token;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenIdTest {

    @Test
    void shouldCreateWithValidUuid() {
        var uuid = UUID.randomUUID();
        var tokenId = new TokenId(uuid);
        assertEquals(uuid, tokenId.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new TokenId(null));
    }

    @Test
    void shouldGenerateUniqueIds() {
        assertNotEquals(TokenId.generate(), TokenId.generate());
    }

    @Test
    void shouldCreateFromValidString() {
        var uuid = UUID.randomUUID();
        assertEquals(uuid, TokenId.fromString(uuid.toString()).value());
    }

    @Test
    void shouldRejectInvalidString() {
        assertThrows(IllegalArgumentException.class, () -> TokenId.fromString("invalid"));
    }

    @Test
    void shouldHaveValueEquality() {
        var uuid = UUID.randomUUID();
        assertEquals(new TokenId(uuid), new TokenId(uuid));
    }
}
