package com.vault.domain.exception;

public class CredentialNotFoundException extends RuntimeException {

    public CredentialNotFoundException(String credentialId) {
        super("Credential not found: " + credentialId);
    }
}
