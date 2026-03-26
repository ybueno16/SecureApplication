package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {

    @Test
    void shouldCreateWithValidTag() {
        var tag = new Tag("finance");
        assertEquals("finance", tag.value());
    }

    @Test
    void shouldNormalizeTolowerCase() {
        var tag = new Tag("FINANCE");
        assertEquals("finance", tag.value());
    }

    @Test
    void shouldTrimWhitespace() {
        var tag = new Tag("  finance  ");
        assertEquals("finance", tag.value());
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    void shouldRejectEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new Tag(""));
    }

    @Test
    void shouldRejectBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Tag("   "));
    }

    @Test
    void shouldRejectExceedingMaxLength() {
        assertThrows(IllegalArgumentException.class, () -> new Tag("a".repeat(51)));
    }

    @Test
    void shouldAcceptMaxLength() {
        assertDoesNotThrow(() -> new Tag("a".repeat(50)));
    }

    @Test
    void shouldSupportStartsWith() {
        var tag = new Tag("work-email");
        assertTrue(tag.startsWith("work"));
        assertFalse(tag.startsWith("personal"));
    }

    @Test
    void shouldStartsWithCaseInsensitive() {
        var tag = new Tag("Work-Email");
        assertTrue(tag.startsWith("WORK"));
    }

    @Test
    void shouldHaveValueEquality() {
        assertEquals(new Tag("finance"), new Tag("FINANCE"));
    }

    @Test
    void shouldNotEqualDifferentTag() {
        assertNotEquals(new Tag("finance"), new Tag("social"));
    }
}
