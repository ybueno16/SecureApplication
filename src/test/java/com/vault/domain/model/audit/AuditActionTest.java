package com.vault.domain.model.audit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuditActionTest {

    @Test
    void shouldHaveAllExpectedValues() {
        var actions = AuditAction.values();
        assertEquals(6, actions.length);
    }

    @Test
    void shouldContainLoginAction() {
        assertEquals(AuditAction.LOGIN, AuditAction.valueOf("LOGIN"));
    }

    @Test
    void shouldContainLogoutAction() {
        assertEquals(AuditAction.LOGOUT, AuditAction.valueOf("LOGOUT"));
    }

    @Test
    void shouldContainCredentialCreateAction() {
        assertEquals(AuditAction.CREDENTIAL_CREATE, AuditAction.valueOf("CREDENTIAL_CREATE"));
    }

    @Test
    void shouldContainCredentialUpdateAction() {
        assertEquals(AuditAction.CREDENTIAL_UPDATE, AuditAction.valueOf("CREDENTIAL_UPDATE"));
    }

    @Test
    void shouldContainCredentialDeleteAction() {
        assertEquals(AuditAction.CREDENTIAL_DELETE, AuditAction.valueOf("CREDENTIAL_DELETE"));
    }

    @Test
    void shouldContainCredentialRevealAction() {
        assertEquals(AuditAction.CREDENTIAL_REVEAL, AuditAction.valueOf("CREDENTIAL_REVEAL"));
    }

    @Test
    void shouldRejectInvalidAction() {
        assertThrows(IllegalArgumentException.class, () -> AuditAction.valueOf("INVALID"));
    }
}
