package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CredentialMetadataTest {

    @Test
    void shouldExposeSiteUrl() {
        var metadata = createMetadata();
        assertEquals("https://example.com", metadata.siteUrl().value());
    }

    @Test
    void shouldExposeTags() {
        var metadata = createMetadata();
        assertEquals(1, metadata.tags().count());
    }

    @Test
    void shouldWithSiteUrlUpdateTimestamp() {
        var metadata = createMetadata();
        var updated = metadata.withSiteUrl(new SiteUrl("https://new.com"));
        assertEquals("https://new.com", updated.siteUrl().value());
        assertFalse(updated.timestamps().updatedAt().isBefore(metadata.timestamps().updatedAt()));
    }

    @Test
    void shouldWithTagsUpdateTimestamp() {
        var metadata = createMetadata();
        var updated = metadata.withTags(Tags.empty());
        assertEquals(0, updated.tags().count());
        assertFalse(updated.timestamps().updatedAt().isBefore(metadata.timestamps().updatedAt()));
    }

    @Test
    void shouldWithUpdatedTimestamp() {
        var metadata = createMetadata();
        var updated = metadata.withUpdatedTimestamp();
        assertFalse(updated.timestamps().updatedAt().isBefore(metadata.timestamps().updatedAt()));
        assertEquals(metadata.siteUrl(), updated.siteUrl());
        assertEquals(metadata.tags(), updated.tags());
    }

    private CredentialMetadata createMetadata() {
        return new CredentialMetadata(
                new SiteUrl("https://example.com"),
                new Tags(List.of(new Tag("test"))),
                Timestamps.now());
    }
}
