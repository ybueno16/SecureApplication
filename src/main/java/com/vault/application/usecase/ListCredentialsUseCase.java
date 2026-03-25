package com.vault.application.usecase;

import com.vault.application.dto.CredentialListResponse;
import com.vault.application.dto.CredentialResponse;
import com.vault.domain.model.credential.Credential;
import com.vault.domain.model.credential.Tag;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.CredentialRepository;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class ListCredentialsUseCase {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private final CredentialRepository credentialRepository;

    public ListCredentialsUseCase(CredentialRepository credentialRepository) {
        this.credentialRepository = credentialRepository;
    }

    public CredentialListResponse execute(UserId userId, String search, String cursor, Integer limit) {
        var pageSize = (limit != null && limit > 0 && limit <= 100) ? limit : DEFAULT_PAGE_SIZE;
        var credentials = credentialRepository.findByUserId(userId, search, cursor, pageSize + 1);

        var hasMore = credentials.size() > pageSize;
        var pageItems = hasMore ? credentials.subList(0, pageSize) : credentials;

        var nextCursor = hasMore ? encodeCursor(pageItems.getLast()) : null;
        return new CredentialListResponse(mapToResponses(pageItems), nextCursor, hasMore);
    }

    private List<CredentialResponse> mapToResponses(List<Credential> credentials) {
        return credentials.stream().map(this::toResponse).toList();
    }

    private CredentialResponse toResponse(Credential credential) {
        return new CredentialResponse(
                credential.toCredentialId().value().toString(),
                credential.toSiteUrl().value(),
                credential.toTags().toUnmodifiableList().stream().map(Tag::value).toList(),
                credential.toTimestamps().createdAt(),
                credential.toTimestamps().updatedAt());
    }

    private String encodeCursor(Credential credential) {
        var raw = credential.toTimestamps().createdAt().toEpochMilli()
                + ":" + credential.toCredentialId().value().toString();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes());
    }
}
