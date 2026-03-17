package com.vault.domain.model.token;

import com.vault.domain.model.shared.Expiration;

public record TokenState(Expiration expiration, boolean revoked) {

    public boolean isUsable() {
        return !revoked && !expiration.isExpired();
    }

    public TokenState revoke() {
        return new TokenState(expiration, true);
    }
}
