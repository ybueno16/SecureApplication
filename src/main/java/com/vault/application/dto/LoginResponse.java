package com.vault.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") long expiresInSeconds,
        @JsonProperty("token_type") String tokenType
) {
    public static LoginResponse of(String accessToken, String refreshToken, long expiresInSeconds) {
        return new LoginResponse(accessToken, refreshToken, expiresInSeconds, "Bearer");
    }
}
