package com.vault.application.usecase;

import com.vault.application.dto.CredentialResponse;
import com.vault.application.dto.UpdateCredentialRequest;
import com.vault.domain.exception.CredentialAccessDeniedException;
import com.vault.domain.exception.CredentialNotFoundException;
import com.vault.domain.model.audit.AuditAction;
import com.vault.domain.model.audit.AuditLog;
import com.vault.domain.model.credential.CredentialId;
import com.vault.domain.model.credential.EncryptedData;
import com.vault.domain.model.credential.SiteUrl;
import com.vault.domain.model.credential.Tag;
import com.vault.domain.model.credential.Tags;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.AuditLogRepository;
import com.vault.domain.repository.CredentialRepository;
import com.vault.domain.repository.UserRepository;
import com.vault.domain.service.CredentialEncryptionService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class UpdateCredentialUseCase {

    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final CredentialEncryptionService encryptionService;
    private final AuditLogRepository auditLogRepository;

    public UpdateCredentialUseCase(CredentialRepository credentialRepository,
                                   UserRepository userRepository,
                                   CredentialEncryptionService encryptionService,
                                   AuditLogRepository auditLogRepository) {
        this.credentialRepository = credentialRepository;
        this.userRepository = userRepository;
        this.encryptionService = encryptionService;
        this.auditLogRepository = auditLogRepository;
    }

    public CredentialResponse execute(String credentialIdRaw, UpdateCredentialRequest request,
                                      UserId userId, String masterPasswordRaw, String ipAddressRaw) {
        var credentialId = CredentialId.fromString(credentialIdRaw);
        var credential = credentialRepository.findById(credentialId)
                .orElseThrow(() -> new CredentialNotFoundException(credentialIdRaw));
        throwIfNotOwner(credential.belongsTo(userId));

        var user = userRepository.findById(userId).orElseThrow();
        var masterPassword = new MasterPassword(masterPasswordRaw.toCharArray());
        var derivedKey = encryptionService.deriveKey(masterPassword, user.toKdfSalt());
        masterPassword.clear();

        credential.updateEncryptedData(encryptFields(request, derivedKey));
        credential.updateMetadata(new SiteUrl(request.siteUrl()), buildTags(request));
        credentialRepository.update(credential);

        auditLogRepository.save(AuditLog.create(
                userId, AuditAction.CREDENTIAL_UPDATE, credentialId, new IpAddress(ipAddressRaw)));

        return toResponse(credential);
    }

    private void throwIfNotOwner(boolean isOwner) {
        if (!isOwner) {
            throw new CredentialAccessDeniedException();
        }
    }

    private EncryptedData encryptFields(UpdateCredentialRequest request, byte[] derivedKey) {
        var encUsername = encryptionService.encrypt(request.username().getBytes(StandardCharsets.UTF_8), derivedKey);
        var encPassword = encryptionService.encrypt(request.password().getBytes(StandardCharsets.UTF_8), derivedKey);
        var notesBytes = (request.notes() != null) ? request.notes().getBytes(StandardCharsets.UTF_8) : new byte[0];
        var encNotes = encryptionService.encrypt(notesBytes, derivedKey);
        return new EncryptedData(encUsername, encPassword, encNotes);
    }

    private Tags buildTags(UpdateCredentialRequest request) {
        var tagList = (request.tags() != null) ? request.tags() : Collections.<String>emptyList();
        return new Tags(tagList.stream().map(Tag::new).toList());
    }

    private CredentialResponse toResponse(com.vault.domain.model.credential.Credential credential) {
        return new CredentialResponse(
                credential.toCredentialId().value().toString(),
                credential.toSiteUrl().value(),
                credential.toTags().toUnmodifiableList().stream().map(Tag::value).toList(),
                credential.toTimestamps().createdAt(),
                credential.toTimestamps().updatedAt());
    }
}
