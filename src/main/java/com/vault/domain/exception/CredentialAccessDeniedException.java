package com.vault.domain.exception;

public class CredentialAccessDeniedException extends RuntimeException {

    public CredentialAccessDeniedException() {
        super("You do not have access to this credential");
    }
}
