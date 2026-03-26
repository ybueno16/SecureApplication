package com.vault.domain.model.credential;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TagsTest {

    @Test
    void shouldCreateEmptyTags() {
        var tags = Tags.empty();
        assertEquals(0, tags.count());
    }

    @Test
    void shouldAddTag() {
        var tags = Tags.empty().add(new Tag("finance"));
        assertEquals(1, tags.count());
        assertTrue(tags.contains(new Tag("finance")));
    }

    @Test
    void shouldNotAddDuplicate() {
        var tags = Tags.empty().add(new Tag("finance")).add(new Tag("finance"));
        assertEquals(1, tags.count());
    }

    @Test
    void shouldRemoveTag() {
        var tags = Tags.empty().add(new Tag("finance")).add(new Tag("social")).remove(new Tag("finance"));
        assertEquals(1, tags.count());
        assertFalse(tags.contains(new Tag("finance")));
        assertTrue(tags.contains(new Tag("social")));
    }

    @Test
    void shouldFilterByPrefix() {
        var tags = new Tags(List.of(new Tag("work-email"), new Tag("work-dev"), new Tag("personal")));
        var filtered = tags.filterByPrefix("work");
        assertEquals(2, filtered.count());
    }

    @Test
    void shouldReturnUnmodifiableList() {
        var tags = Tags.empty().add(new Tag("test"));
        var list = tags.toUnmodifiableList();
        assertThrows(UnsupportedOperationException.class, () -> list.add(new Tag("hack")));
    }

    @Test
    void shouldCreateFromCommaSeparated() {
        var tags = Tags.fromCommaSeparated("finance,social,work");
        assertEquals(3, tags.count());
    }

    @Test
    void shouldHandleBlankCommaSeparated() {
        assertEquals(0, Tags.fromCommaSeparated("").count());
        assertEquals(0, Tags.fromCommaSeparated(null).count());
    }

    @Test
    void shouldSerializeToCommaSeparated() {
        var tags = new Tags(List.of(new Tag("a"), new Tag("b"), new Tag("c")));
        assertEquals("a,b,c", tags.toCommaSeparated());
    }

    @Test
    void shouldBeImmutableOnAdd() {
        var original = Tags.empty();
        var modified = original.add(new Tag("test"));
        assertEquals(0, original.count());
        assertEquals(1, modified.count());
    }
}
