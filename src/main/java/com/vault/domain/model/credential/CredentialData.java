package com.vault.domain.model.credential;

public record CredentialData(EncryptedData encryptedData, CredentialMetadata metadata) {

    public CredentialData withEncryptedData(EncryptedData newEncryptedData) {
        return new CredentialData(newEncryptedData, metadata.withUpdatedTimestamp());
    }

    public CredentialData withMetadata(CredentialMetadata newMetadata) {
        return new CredentialData(encryptedData, newMetadata);
    }
}
