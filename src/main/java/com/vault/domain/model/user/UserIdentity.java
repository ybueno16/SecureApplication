package com.vault.domain.model.user;

public record UserIdentity(UserId userId, Email email) {

    public boolean matchesId(UserId other) {
        return userId.equals(other);
    }
}
