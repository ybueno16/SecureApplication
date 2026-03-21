package com.vault.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimiterService {

    private final Cache<String, Bucket> ipBuckets;
    private final Cache<String, Bucket> usernameBuckets;
    private final int loginAttemptsPerMinute;
    private final int loginAttemptsPerHour;

    public RateLimiterService(
            @Value("${app.security.rate-limit.login-attempts-per-minute:5}") int loginAttemptsPerMinute,
            @Value("${app.security.rate-limit.login-attempts-per-hour:10}") int loginAttemptsPerHour) {
        this.loginAttemptsPerMinute = loginAttemptsPerMinute;
        this.loginAttemptsPerHour = loginAttemptsPerHour;
        this.ipBuckets = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(Duration.ofMinutes(10))
                .build();
        this.usernameBuckets = Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterAccess(Duration.ofHours(2))
                .build();
    }

    public boolean isIpAllowed(String ipAddress) {
        var bucket = ipBuckets.get(ipAddress, this::createIpBucket);
        return bucket.tryConsume(1);
    }

    public boolean isUsernameAllowed(String username) {
        var bucket = usernameBuckets.get(username, this::createUsernameBucket);
        return bucket.tryConsume(1);
    }

    private Bucket createIpBucket(String key) {
        var limit = Bandwidth.builder()
                .capacity(loginAttemptsPerMinute)
                .refillGreedy(loginAttemptsPerMinute, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createUsernameBucket(String key) {
        var limit = Bandwidth.builder()
                .capacity(loginAttemptsPerHour)
                .refillGreedy(loginAttemptsPerHour, Duration.ofHours(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
