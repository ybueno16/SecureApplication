package com.vault.domain.model.credential;

import java.util.Objects;
import java.util.UUID;

public record CredentialId(UUID value) {

    public CredentialId {
        Objects.requireNonNull(value, "CredentialId must not be null");
    }

    public static CredentialId generate() {
        return new CredentialId(UUID.randomUUID());
    }

    public static CredentialId fromString(String raw) {
        return new CredentialId(UUID.fromString(raw));
    }
}
