package com.vault.domain.model.credential;

import com.vault.domain.model.user.UserId;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CredentialTest {

    @Test
    void shouldCreateViaFactory() {
        var userId = UserId.generate();
        var credId = CredentialId.generate();
        var credential = Credential.create(
                credId, userId, new SiteUrl("https://github.com"), createEncryptedData(), Tags.empty());

        assertEquals(credId, credential.toCredentialId());
        assertTrue(credential.belongsTo(userId));
        assertEquals("https://github.com", credential.toSiteUrl().value());
    }

    @Test
    void shouldNotBelongToDifferentUser() {
        var credential = createTestCredential();
        assertFalse(credential.belongsTo(UserId.generate()));
    }

    @Test
    void shouldUpdateEncryptedData() {
        var credential = createTestCredential();
        var newEncrypted = createEncryptedData();
        credential.updateEncryptedData(newEncrypted);
        assertEquals(newEncrypted.usernameField(), credential.encryptedUsername());
    }

    @Test
    void shouldUpdateMetadata() {
        var credential = createTestCredential();
        var newUrl = new SiteUrl("https://new-site.com");
        var newTags = new Tags(List.of(new Tag("updated")));
        credential.updateMetadata(newUrl, newTags);

        assertEquals("https://new-site.com", credential.toSiteUrl().value());
        assertEquals(1, credential.toTags().count());
    }

    @Test
    void shouldExposeEncryptedFields() {
        var encrypted = createEncryptedData();
        var credential = Credential.create(
                CredentialId.generate(), UserId.generate(), new SiteUrl("https://a.com"), encrypted, Tags.empty());

        assertNotNull(credential.encryptedUsername());
        assertNotNull(credential.encryptedPassword());
    }

    @Test
    void shouldExposeTimestamps() {
        var credential = createTestCredential();
        assertNotNull(credential.toTimestamps());
        assertNotNull(credential.toTimestamps().createdAt());
        assertNotNull(credential.toTimestamps().updatedAt());
    }

    @Test
    void shouldRejectNullIdentity() {
        assertThrows(NullPointerException.class, () -> new Credential(null, null));
    }

    @Test
    void shouldUpdateTimestampOnMetadataChange() {
        var credential = createTestCredential();
        var originalUpdated = credential.toTimestamps().updatedAt();
        credential.updateMetadata(new SiteUrl("https://changed.com"), Tags.empty());
        assertFalse(credential.toTimestamps().updatedAt().isBefore(originalUpdated));
    }

    private Credential createTestCredential() {
        return Credential.create(
                CredentialId.generate(), UserId.generate(),
                new SiteUrl("https://test.com"), createEncryptedData(), Tags.empty());
    }

    private EncryptedData createEncryptedData() {
        var field = new EncryptedField(new byte[]{1, 2, 3}, new byte[]{4, 5, 6});
        return new EncryptedData(field, field, null);
    }
}
