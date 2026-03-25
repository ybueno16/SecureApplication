package com.vault.application.dto;

public record RevealedCredentialResponse(
        String username,
        String password,
        String notes
) {
}
