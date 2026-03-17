package com.vault.domain.model.audit;

public enum AuditAction {
    LOGIN,
    LOGOUT,
    CREDENTIAL_CREATE,
    CREDENTIAL_UPDATE,
    CREDENTIAL_DELETE,
    CREDENTIAL_REVEAL
}
