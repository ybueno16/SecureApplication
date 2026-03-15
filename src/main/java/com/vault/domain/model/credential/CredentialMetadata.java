package com.vault.domain.model.credential;

public record CredentialMetadata(SiteUrl siteUrl, Tags tags, Timestamps timestamps) {

    public CredentialMetadata withSiteUrl(SiteUrl newSiteUrl) {
        return new CredentialMetadata(newSiteUrl, tags, timestamps.withUpdatedNow());
    }

    public CredentialMetadata withTags(Tags newTags) {
        return new CredentialMetadata(siteUrl, newTags, timestamps.withUpdatedNow());
    }

    public CredentialMetadata withUpdatedTimestamp() {
        return new CredentialMetadata(siteUrl, tags, timestamps.withUpdatedNow());
    }
}
