package com.vault.domain.service;

import com.vault.domain.model.shared.PasswordPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorServiceTest {

    private PasswordGeneratorService service;

    @BeforeEach
    void setUp() {
        service = new PasswordGeneratorService();
    }

    @ParameterizedTest
    @ValueSource(ints = {8, 16, 24, 64, 128})
    void shouldGeneratePasswordWithRequestedLength(int length) {
        var policy = new PasswordPolicy(length, true, false);
        var password = service.generate(policy);
        assertEquals(length, password.length());
    }

    @Test
    void shouldIncludeSymbolsWhenRequested() {
        var policy = new PasswordPolicy(100, true, false);
        var password = service.generate(policy);
        assertTrue(password.chars().anyMatch(c -> "!@#$%^&*()-_=+[]{}|;:,.<>?".indexOf(c) >= 0),
                "Password should contain at least one symbol");
    }

    @Test
    void shouldExcludeSymbolsWhenNotRequested() {
        var policy = new PasswordPolicy(100, false, false);
        var password = service.generate(policy);
        assertTrue(password.chars().allMatch(c -> Character.isLetterOrDigit(c)),
                "Password should only contain letters and digits");
    }

    @Test
    void shouldExcludeAmbiguousCharacters() {
        var policy = new PasswordPolicy(100, false, true);
        var password = service.generate(policy);
        assertTrue(password.chars().noneMatch(c -> "O0Il1".indexOf(c) >= 0),
                "Password should not contain ambiguous characters");
    }

    @Test
    void shouldGenerateUniquePasswords() {
        var policy = new PasswordPolicy(32, true, false);
        var first = service.generate(policy);
        var second = service.generate(policy);
        assertNotEquals(first, second, "Two generated passwords should differ");
    }

    @Test
    void shouldIncludeLettersAndDigits() {
        var policy = new PasswordPolicy(100, false, false);
        var password = service.generate(policy);
        assertTrue(password.chars().anyMatch(Character::isUpperCase));
        assertTrue(password.chars().anyMatch(Character::isLowerCase));
        assertTrue(password.chars().anyMatch(Character::isDigit));
    }
}
