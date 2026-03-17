package com.vault.domain.model.token;

import com.vault.domain.model.shared.Expiration;
import com.vault.domain.model.user.UserId;

import java.util.Objects;

public final class RefreshToken {

    private final TokenIdentity identity;
    private TokenState state;

    public RefreshToken(TokenIdentity identity, TokenState state) {
        Objects.requireNonNull(identity, "TokenIdentity must not be null");
        Objects.requireNonNull(state, "TokenState must not be null");
        this.identity = identity;
        this.state = state;
    }

    public static RefreshToken create(TokenId tokenId, UserId userId,
                                       TokenHash hash, Expiration expiration) {
        var tokenIdentity = new TokenIdentity(tokenId, userId, hash);
        var tokenState = new TokenState(expiration, false);
        return new RefreshToken(tokenIdentity, tokenState);
    }

    public boolean isUsable() {
        return state.isUsable();
    }

    public void revoke() {
        this.state = state.revoke();
    }

    public boolean belongsTo(UserId userId) {
        return identity.userId().equals(userId);
    }

    public TokenId toTokenId() { return identity.tokenId(); }
    public UserId toUserId() { return identity.userId(); }
    public TokenHash toTokenHash() { return identity.tokenHash(); }
    public Expiration toExpiration() { return state.expiration(); }
    public boolean isRevoked() { return state.revoked(); }
}
