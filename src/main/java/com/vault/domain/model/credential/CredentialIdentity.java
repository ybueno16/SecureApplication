package com.vault.domain.model.credential;

import com.vault.domain.model.user.UserId;

public record CredentialIdentity(CredentialId credentialId, UserId userId) {

    public boolean belongsTo(UserId other) {
        return userId.equals(other);
    }
}
