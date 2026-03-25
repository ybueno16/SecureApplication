package com.vault.application.usecase;

import com.vault.application.dto.GeneratePasswordRequest;
import com.vault.application.dto.GeneratePasswordResponse;
import com.vault.domain.model.shared.PasswordPolicy;
import com.vault.domain.service.PasswordGeneratorService;
import org.springframework.stereotype.Service;

@Service
public class GeneratePasswordUseCase {

    private final PasswordGeneratorService passwordGeneratorService;

    public GeneratePasswordUseCase(PasswordGeneratorService passwordGeneratorService) {
        this.passwordGeneratorService = passwordGeneratorService;
    }

    public GeneratePasswordResponse execute(GeneratePasswordRequest request) {
        var policy = new PasswordPolicy(
                request.effectiveLength(),
                request.effectiveSymbols(),
                request.effectiveAmbiguous());
        var generated = passwordGeneratorService.generate(policy);
        return new GeneratePasswordResponse(generated);
    }
}
