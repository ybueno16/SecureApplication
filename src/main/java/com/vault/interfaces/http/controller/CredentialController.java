package com.vault.interfaces.http.controller;

import com.vault.application.dto.CreateCredentialRequest;
import com.vault.application.dto.CredentialListResponse;
import com.vault.application.dto.CredentialResponse;
import com.vault.application.dto.RevealedCredentialResponse;
import com.vault.application.dto.UpdateCredentialRequest;
import com.vault.application.usecase.CreateCredentialUseCase;
import com.vault.application.usecase.DeleteCredentialUseCase;
import com.vault.application.usecase.ListCredentialsUseCase;
import com.vault.application.usecase.RevealCredentialUseCase;
import com.vault.application.usecase.UpdateCredentialUseCase;
import com.vault.domain.model.user.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/credentials")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Credentials", description = "CRUD operations for vault credentials")
public class CredentialController {

    private final CreateCredentialUseCase createCredentialUseCase;
    private final UpdateCredentialUseCase updateCredentialUseCase;
    private final DeleteCredentialUseCase deleteCredentialUseCase;
    private final ListCredentialsUseCase listCredentialsUseCase;
    private final RevealCredentialUseCase revealCredentialUseCase;

    public CredentialController(CreateCredentialUseCase createCredentialUseCase,
                                UpdateCredentialUseCase updateCredentialUseCase,
                                DeleteCredentialUseCase deleteCredentialUseCase,
                                ListCredentialsUseCase listCredentialsUseCase,
                                RevealCredentialUseCase revealCredentialUseCase) {
        this.createCredentialUseCase = createCredentialUseCase;
        this.updateCredentialUseCase = updateCredentialUseCase;
        this.deleteCredentialUseCase = deleteCredentialUseCase;
        this.listCredentialsUseCase = listCredentialsUseCase;
        this.revealCredentialUseCase = revealCredentialUseCase;
    }

    @PostMapping
    @Operation(summary = "Create credential", description = "Store an encrypted credential in the vault")
    public ResponseEntity<CredentialResponse> create(
            @Valid @RequestBody CreateCredentialRequest request,
            @RequestHeader("X-Master-Password") String masterPassword,
            @AuthenticationPrincipal UserId userId,
            HttpServletRequest httpRequest) {
        var response = createCredentialUseCase.execute(request, userId, masterPassword, extractClientIp(httpRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update credential", description = "Update an existing vault credential")
    public ResponseEntity<CredentialResponse> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateCredentialRequest request,
            @RequestHeader("X-Master-Password") String masterPassword,
            @AuthenticationPrincipal UserId userId,
            HttpServletRequest httpRequest) {
        var response = updateCredentialUseCase.execute(id, request, userId, masterPassword, extractClientIp(httpRequest));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete credential", description = "Remove a credential from the vault")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @AuthenticationPrincipal UserId userId,
            HttpServletRequest httpRequest) {
        deleteCredentialUseCase.execute(id, userId, extractClientIp(httpRequest));
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List credentials", description = "Cursor-based paginated credential listing")
    public ResponseEntity<CredentialListResponse> list(
            @AuthenticationPrincipal UserId userId,
            @RequestParam(required = false) @Parameter(description = "Search by site URL") String search,
            @RequestParam(required = false) @Parameter(description = "Pagination cursor") String cursor,
            @RequestParam(required = false) @Parameter(description = "Page size (max 100)") Integer limit) {
        return ResponseEntity.ok(listCredentialsUseCase.execute(userId, search, cursor, limit));
    }

    @GetMapping("/{id}/reveal")
    @Operation(summary = "Reveal credential", description = "Decrypt and return stored username, password, notes")
    public ResponseEntity<RevealedCredentialResponse> reveal(
            @PathVariable String id,
            @RequestHeader("X-Master-Password") String masterPassword,
            @AuthenticationPrincipal UserId userId,
            HttpServletRequest httpRequest) {
        var response = revealCredentialUseCase.execute(id, userId, masterPassword, extractClientIp(httpRequest));
        return ResponseEntity.ok(response);
    }

    private String extractClientIp(HttpServletRequest request) {
        var forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
