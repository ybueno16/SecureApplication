package com.vault.domain.model.token;

import com.vault.domain.model.user.UserId;

public record TokenIdentity(TokenId tokenId, UserId userId, TokenHash tokenHash) {
}
