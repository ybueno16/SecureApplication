package com.vault.domain.model.user;

public record PasswordData(PasswordHash passwordHash, KdfSalt kdfSalt) {
}
