package com.vault.domain.model.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class IpAddressTest {

    @ParameterizedTest
    @ValueSource(strings = {"192.168.1.1", "0.0.0.0", "255.255.255.255", "10.0.0.1", "127.0.0.1"})
    void shouldAcceptValidIpv4(String ip) {
        assertDoesNotThrow(() -> new IpAddress(ip));
    }

    @Test
    void shouldRejectNull() {
        assertThrows(NullPointerException.class, () -> new IpAddress(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "256.1.1.1", "not-an-ip", "192.168.1", "192.168.1.1.1"})
    void shouldRejectInvalidAddress(String ip) {
        assertThrows(IllegalArgumentException.class, () -> new IpAddress(ip));
    }

    @Test
    void shouldStoreValue() {
        var ip = new IpAddress("10.0.0.1");
        assertEquals("10.0.0.1", ip.value());
    }

    @Test
    void shouldHaveValueEquality() {
        assertEquals(new IpAddress("192.168.1.1"), new IpAddress("192.168.1.1"));
    }

    @Test
    void shouldNotEqualDifferentAddress() {
        assertNotEquals(new IpAddress("192.168.1.1"), new IpAddress("10.0.0.1"));
    }

    @Test
    void shouldAcceptLoopbackIpv6() {
        assertDoesNotThrow(() -> new IpAddress("::"));
    }
}
