package com.vault.domain.model.credential;

import com.vault.domain.model.user.UserId;

import java.util.Objects;

public final class Credential {

    private final CredentialIdentity identity;
    private CredentialData data;

    public Credential(CredentialIdentity identity, CredentialData data) {
        Objects.requireNonNull(identity, "CredentialIdentity must not be null");
        Objects.requireNonNull(data, "CredentialData must not be null");
        this.identity = identity;
        this.data = data;
    }

    public static Credential create(CredentialId id, UserId userId, SiteUrl siteUrl,
                                     EncryptedData encrypted, Tags tags) {
        var credIdentity = new CredentialIdentity(id, userId);
        var metadata = new CredentialMetadata(siteUrl, tags, Timestamps.now());
        return new Credential(credIdentity, new CredentialData(encrypted, metadata));
    }

    public boolean belongsTo(UserId userId) {
        return identity.belongsTo(userId);
    }

    public void updateEncryptedData(EncryptedData newEncrypted) {
        this.data = data.withEncryptedData(newEncrypted);
    }

    public void updateMetadata(SiteUrl siteUrl, Tags tags) {
        var newMetadata = new CredentialMetadata(siteUrl, tags, data.metadata().timestamps().withUpdatedNow());
        this.data = data.withMetadata(newMetadata);
    }

    public EncryptedField encryptedPassword() { return data.encryptedData().passwordField(); }

    public EncryptedField encryptedUsername() { return data.encryptedData().usernameField(); }

    public EncryptedField encryptedNotes() { return data.encryptedData().notesField(); }

    public CredentialId toCredentialId() { return identity.credentialId(); }
    public UserId toUserId() { return identity.userId(); }
    public SiteUrl toSiteUrl() { return data.metadata().siteUrl(); }
    public Tags toTags() { return data.metadata().tags(); }
    public Timestamps toTimestamps() { return data.metadata().timestamps(); }
    public EncryptedData toEncryptedData() { return data.encryptedData(); }
}
