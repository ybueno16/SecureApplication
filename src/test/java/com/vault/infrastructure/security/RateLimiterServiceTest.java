package com.vault.infrastructure.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;

    @BeforeEach
    void setUp() {
        rateLimiterService = new RateLimiterService(5, 10);
    }

    @Test
    void shouldAllowRequestsWithinIpLimit() {
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiterService.isIpAllowed("192.168.1.1"));
        }
    }

    @Test
    void shouldBlockIpAfterExceedingLimit() {
        for (int i = 0; i < 5; i++) {
            rateLimiterService.isIpAllowed("10.0.0.1");
        }
        assertFalse(rateLimiterService.isIpAllowed("10.0.0.1"));
    }

    @Test
    void shouldTrackIpsIndependently() {
        for (int i = 0; i < 5; i++) {
            rateLimiterService.isIpAllowed("10.0.0.1");
        }
        assertTrue(rateLimiterService.isIpAllowed("10.0.0.2"));
    }

    @Test
    void shouldAllowRequestsWithinUsernameLimit() {
        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiterService.isUsernameAllowed("user@example.com"));
        }
    }

    @Test
    void shouldBlockUsernameAfterExceedingLimit() {
        for (int i = 0; i < 10; i++) {
            rateLimiterService.isUsernameAllowed("victim@example.com");
        }
        assertFalse(rateLimiterService.isUsernameAllowed("victim@example.com"));
    }

    @Test
    void shouldTrackUsernamesIndependently() {
        for (int i = 0; i < 10; i++) {
            rateLimiterService.isUsernameAllowed("user1@example.com");
        }
        assertTrue(rateLimiterService.isUsernameAllowed("user2@example.com"));
    }
}
