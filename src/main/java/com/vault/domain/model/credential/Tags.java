package com.vault.domain.model.credential;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Tags {

    private final List<Tag> values;

    public Tags(List<Tag> values) {
        Objects.requireNonNull(values, "Tags list must not be null");
        this.values = List.copyOf(values);
    }

    public static Tags empty() {
        return new Tags(List.of());
    }

    public static Tags fromCommaSeparated(String raw) {
        if (raw == null || raw.isBlank()) {
            return empty();
        }
        var tagList = java.util.Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Tag::new)
                .toList();
        return new Tags(tagList);
    }

    public Tags add(Tag tag) {
        if (contains(tag)) return this;
        var newList = new ArrayList<>(values);
        newList.add(tag);
        return new Tags(newList);
    }

    public Tags remove(Tag tag) {
        return new Tags(values.stream().filter(t -> !t.equals(tag)).toList());
    }

    public boolean contains(Tag tag) {
        return values.contains(tag);
    }

    public int count() {
        return values.size();
    }

    public Tags filterByPrefix(String prefix) {
        return new Tags(values.stream().filter(t -> t.startsWith(prefix)).toList());
    }

    public List<Tag> toUnmodifiableList() {
        return values;
    }

    public String toCommaSeparated() {
        return values.stream().map(Tag::value).collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Tags that)) return false;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }
}
