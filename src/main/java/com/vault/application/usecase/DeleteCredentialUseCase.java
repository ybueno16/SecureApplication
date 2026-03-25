package com.vault.application.usecase;

import com.vault.domain.exception.CredentialAccessDeniedException;
import com.vault.domain.exception.CredentialNotFoundException;
import com.vault.domain.model.audit.AuditAction;
import com.vault.domain.model.audit.AuditLog;
import com.vault.domain.model.credential.CredentialId;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.AuditLogRepository;
import com.vault.domain.repository.CredentialRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteCredentialUseCase {

    private final CredentialRepository credentialRepository;
    private final AuditLogRepository auditLogRepository;

    public DeleteCredentialUseCase(CredentialRepository credentialRepository,
                                   AuditLogRepository auditLogRepository) {
        this.credentialRepository = credentialRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public void execute(String credentialIdRaw, UserId userId, String ipAddressRaw) {
        var credentialId = CredentialId.fromString(credentialIdRaw);
        var credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new CredentialNotFoundException(credentialIdRaw));
        throwIfNotOwner(credential.belongsTo(userId));

        credentialRepository.deleteById(credentialId);
        auditLogRepository.save(AuditLog.create(
                userId, AuditAction.CREDENTIAL_DELETE, credentialId, new IpAddress(ipAddressRaw)));
    }

    private void throwIfNotOwner(boolean isOwner) {
        if (!isOwner) {
            throw new CredentialAccessDeniedException();
        }
    }
}
