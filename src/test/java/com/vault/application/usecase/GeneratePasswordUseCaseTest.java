package com.vault.application.usecase;

import com.vault.application.dto.GeneratePasswordRequest;
import com.vault.domain.service.PasswordGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeneratePasswordUseCaseTest {

    private GeneratePasswordUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GeneratePasswordUseCase(new PasswordGeneratorService());
    }

    @Test
    void shouldGenerateWithDefaults() {
        var request = new GeneratePasswordRequest(null, null, null);
        var response = useCase.execute(request);
        assertNotNull(response.generatedPassword());
        assertEquals(24, response.generatedPassword().length());
    }

    @Test
    void shouldGenerateWithCustomLength() {
        var request = new GeneratePasswordRequest(32, null, null);
        var response = useCase.execute(request);
        assertEquals(32, response.generatedPassword().length());
    }

    @Test
    void shouldGenerateWithoutSymbols() {
        var request = new GeneratePasswordRequest(64, false, null);
        var response = useCase.execute(request);
        assertTrue(response.generatedPassword().chars().allMatch(Character::isLetterOrDigit));
    }

    @Test
    void shouldGenerateWithExcludeAmbiguous() {
        var request = new GeneratePasswordRequest(100, false, true);
        var response = useCase.execute(request);
        assertTrue(response.generatedPassword().chars().noneMatch(c -> "O0Il1".indexOf(c) >= 0));
    }

    @Test
    void shouldRejectInvalidLength() {
        var request = new GeneratePasswordRequest(7, null, null);
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(request));
    }

    @Test
    void shouldRejectTooLongLength() {
        var request = new GeneratePasswordRequest(129, null, null);
        assertThrows(IllegalArgumentException.class, () -> useCase.execute(request));
    }
}
