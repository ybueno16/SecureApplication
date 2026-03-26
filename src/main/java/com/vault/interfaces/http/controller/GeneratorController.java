package com.vault.interfaces.http.controller;

import com.vault.application.dto.GeneratePasswordRequest;
import com.vault.application.dto.GeneratePasswordResponse;
import com.vault.application.usecase.GeneratePasswordUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/generator")
@Tag(name = "Password Generator", description = "Generate secure random passwords")
public class GeneratorController {

    private final GeneratePasswordUseCase generatePasswordUseCase;

    public GeneratorController(GeneratePasswordUseCase generatePasswordUseCase) {
        this.generatePasswordUseCase = generatePasswordUseCase;
    }

    @GetMapping
    @Operation(summary = "Generate password", description = "Generate a cryptographically secure random password")
    public ResponseEntity<GeneratePasswordResponse> generate(
            @RequestParam(required = false) @Parameter(description = "Length (8-128, default 24)") Integer length,
            @RequestParam(required = false) @Parameter(description = "Include symbols (default true)") Boolean symbols,
            @RequestParam(required = false) @Parameter(description = "Exclude ambiguous chars (default false)") Boolean ambiguous) {
        var request = new GeneratePasswordRequest(length, symbols, ambiguous);
        return ResponseEntity.ok(generatePasswordUseCase.execute(request));
    }
}
