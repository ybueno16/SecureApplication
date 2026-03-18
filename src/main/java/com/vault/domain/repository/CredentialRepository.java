package com.vault.domain.repository;

import com.vault.domain.model.credential.Credential;
import com.vault.domain.model.credential.CredentialId;
import com.vault.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

public interface CredentialRepository {

    void save(Credential credential);

    void update(Credential credential);

    void deleteById(CredentialId credentialId);

    Optional<Credential> findById(CredentialId credentialId);

    List<Credential> findByUserId(UserId userId, String searchTerm, String cursor, int limit);

    int countByUserId(UserId userId);
}
