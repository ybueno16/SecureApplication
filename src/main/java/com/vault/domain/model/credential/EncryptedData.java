package com.vault.domain.model.credential;

import java.util.Objects;

public final class EncryptedData {

    private final EncryptedField encryptedUsername;
    private final EncryptedField encryptedPassword;
    private final EncryptedField encryptedNotes;

    public EncryptedData(EncryptedField encryptedUsername,
                         EncryptedField encryptedPassword,
                         EncryptedField encryptedNotes) {
        Objects.requireNonNull(encryptedUsername, "Encrypted username must not be null");
        Objects.requireNonNull(encryptedPassword, "Encrypted password must not be null");
        this.encryptedUsername = encryptedUsername;
        this.encryptedPassword = encryptedPassword;
        this.encryptedNotes = encryptedNotes;
    }

    public EncryptedField usernameField() { return encryptedUsername; }

    public EncryptedField passwordField() { return encryptedPassword; }

    public EncryptedField notesField() { return encryptedNotes; }

    @Override
    public String toString() {
        return "EncryptedData[REDACTED]";
    }
}
