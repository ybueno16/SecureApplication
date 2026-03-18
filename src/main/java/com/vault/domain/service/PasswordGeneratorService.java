package com.vault.domain.service;

import com.vault.domain.model.shared.PasswordPolicy;

import java.security.SecureRandom;

public final class PasswordGeneratorService {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String AMBIGUOUS = "O0Il1";

    private final SecureRandom secureRandom;

    public PasswordGeneratorService() {
        this.secureRandom = new SecureRandom();
    }

    public String generate(PasswordPolicy policy) {
        var characters = buildCharacterSet(policy);
        return buildPassword(characters, policy.length());
    }

    private String buildCharacterSet(PasswordPolicy policy) {
        var base = UPPERCASE + LOWERCASE + DIGITS;
        var withSymbols = policy.includeSymbols() ? base + SYMBOLS : base;
        return policy.excludeAmbiguous() ? removeAmbiguous(withSymbols) : withSymbols;
    }

    private String removeAmbiguous(String characters) {
        return characters.chars()
                .filter(c -> AMBIGUOUS.indexOf(c) < 0)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private String buildPassword(String characters, int length) {
        return secureRandom.ints(length, 0, characters.length())
                .mapToObj(characters::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
