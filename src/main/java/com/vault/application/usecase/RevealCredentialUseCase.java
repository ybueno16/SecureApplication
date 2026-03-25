package com.vault.application.usecase;

import com.vault.application.dto.RevealedCredentialResponse;
import com.vault.domain.exception.CredentialAccessDeniedException;
import com.vault.domain.exception.CredentialNotFoundException;
import com.vault.domain.model.audit.AuditAction;
import com.vault.domain.model.audit.AuditLog;
import com.vault.domain.model.credential.CredentialId;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.AuditLogRepository;
import com.vault.domain.repository.CredentialRepository;
import com.vault.domain.repository.UserRepository;
import com.vault.domain.service.CredentialEncryptionService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class RevealCredentialUseCase {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final CredentialEncryptionService encryptionService;
    private final AuditLogRepository auditLogRepository;

    public RevealCredentialUseCase(CredentialRepository credentialRepository,
                                   UserRepository userRepository,
                                   CredentialEncryptionService encryptionService,
                                   AuditLogRepository auditLogRepository) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.auditLogRepository = auditLogRepository;
    }

    public RevealedCredentialResponse execute(String credentialIdRaw, UserId userId,
                                               String masterPasswordRaw, String ipAddressRaw) {
        var credentialId = CredentialId.fromString(credentialIdRaw);
        var credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new CredentialNotFoundException(credentialIdRaw));
        throwIfNotOwner(credential.belongsTo(userId));

        var user = userRepository.findById(userId).orElseThrow();
        var masterPassword = new MasterPassword(masterPasswordRaw.toCharArray());
        var derivedKey = encryptionService.deriveKey(masterPassword, user.toKdfSalt());
        masterPassword.clear();

        var username = decryptToString(credential.encryptedUsername(), derivedKey);
        var password = decryptToString(credential.encryptedPassword(), derivedKey);
        var notes = decryptNotes(credential.encryptedNotes(), derivedKey);

        auditLogRepository.save(AuditLog.create(
                userId, AuditAction.CREDENTIAL_REVEAL, credentialId, new IpAddress(ipAddressRaw)));

        return new RevealedCredentialResponse(username, password, notes);
    }

    private void throwIfNotOwner(boolean isOwner) {
        if (!isOwner) {
            throw new CredentialAccessDeniedException();
        }
    }

    private String decryptToString(com.vault.domain.model.credential.EncryptedField field, byte[] key) {
        return new String(encryptionService.decrypt(field, key), StandardCharsets.UTF_8);
    }

    private String decryptNotes(com.vault.domain.model.credential.EncryptedField field, byte[] key) {
        if (field == null) return null;
        var decrypted = encryptionService.decrypt(field, key);
        return (decrypted.length == 0) ? null : new String(decrypted, StandardCharsets.UTF_8);
    }
}
