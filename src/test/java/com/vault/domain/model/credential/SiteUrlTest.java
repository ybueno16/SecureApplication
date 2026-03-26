package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class SiteUrlTest {

    @Test
    void shouldCreateWithValidUrl() {
        var url = new SiteUrl("https://example.com");
        assertEquals("https://example.com", url.value());
    }

    @Test
    void shouldTrimWhitespace() {
        var url = new SiteUrl("  https://example.com  ");
        assertEquals("https://example.com", url.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new SiteUrl(null));
    }

    @Test
    void shouldRejectEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new SiteUrl(""));
    }

    @Test
    void shouldRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> new SiteUrl("   "));
    }

    @Test
    void shouldRejectExceedingMaxLength() {
        var longUrl = "https://" + "a".repeat(2041);
        assertThrows(IllegalArgumentException.class, () -> new SiteUrl(longUrl));
    }

    @ParameterizedTest
    @ValueSource(strings = {"https://github.com", "http://localhost:8080", "ftp://files.example.com", "example.com"})
    void shouldAcceptVariousFormats(String url) {
        assertDoesNotThrow(() -> new SiteUrl(url));
    }

    @Test
    void shouldHaveValueEquality() {
        assertEquals(new SiteUrl("https://a.com"), new SiteUrl("https://a.com"));
    }

    @Test
    void shouldNotEqualDifferentUrl() {
        assertNotEquals(new SiteUrl("https://a.com"), new SiteUrl("https://b.com"));
    }

    @Test
    void shouldAcceptMaxLength() {
        var maxUrl = "x".repeat(2048);
        assertDoesNotThrow(() -> new SiteUrl(maxUrl));
    }
}
