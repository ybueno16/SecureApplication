package com.vault.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public record AppProperties(SecurityProperties security, CryptoProperties crypto) {

    public record SecurityProperties(
            JwtProperties jwt,
            String allowedOrigins,
            RateLimitProperties rateLimit,
            AccountLockProperties accountLock) {

        public record JwtProperties(String issuer, long accessTokenExpirationMinutes, long refreshTokenExpirationDays) {}
        public record RateLimitProperties(int loginAttemptsPerMinute, int loginAttemptsPerHour) {}
        public record AccountLockProperties(int maxFailedAttempts, int lockDurationMinutes) {}
    }

    public record CryptoProperties(int pbkdf2Iterations) {}

    public List<String> allowedOriginsList() {
        if (security.allowedOrigins == null) return List.of();
        return List.of(security.allowedOrigins.split(","));
    }
}
