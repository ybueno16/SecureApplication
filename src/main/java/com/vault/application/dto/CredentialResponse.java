package com.vault.application.dto;

import java.time.Instant;
import java.util.List;

public record CredentialResponse(
        String id,
        String siteUrl,
        List<String> tags,
        Instant createdAt,
        Instant updatedAt
) {
}
