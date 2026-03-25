package com.vault.application.dto;

import java.util.List;

public record CredentialListResponse(
        List<CredentialResponse> items,
        String nextCursor,
        boolean hasMore
) {
}
