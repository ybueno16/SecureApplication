package com.vault.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateCredentialRequest(
        @NotBlank @Size(max = 2048)
        String siteUrl,

        @NotBlank
        String username,

        @NotBlank
        String password,

        String notes,

        List<String> tags
) {
}
