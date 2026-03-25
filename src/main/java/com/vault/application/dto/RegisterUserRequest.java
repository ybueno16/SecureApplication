package com.vault.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank @Email @Size(max = 255)
        String email,

        @NotBlank @Size(min = 12, max = 128)
        String masterPassword
) {
}
