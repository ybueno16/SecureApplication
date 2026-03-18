package com.vault.domain.repository;

import com.vault.domain.model.token.RefreshToken;
import com.vault.domain.model.token.TokenHash;
import com.vault.domain.model.user.UserId;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(RefreshToken token);

    void update(RefreshToken token);

    Optional<RefreshToken> findByTokenHash(TokenHash tokenHash);

    void revokeAllByUserId(UserId userId);
}
