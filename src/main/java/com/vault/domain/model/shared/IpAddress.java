package com.vault.domain.model.shared;

import java.util.Objects;
import java.util.regex.Pattern;

public record IpAddress(String value) {

    private static final Pattern IPV4 = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

    private static final Pattern IPV6 = Pattern.compile(
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::$|^(([0-9a-fA-F]{1,4}:)*:([0-9a-fA-F]{1,4}:)*[0-9a-fA-F]{1,4})$");

    public IpAddress {
        Objects.requireNonNull(value, "IpAddress must not be null");
        if (!IPV4.matcher(value).matches() && !IPV6.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid IP address: " + value);
        }
    }
}
