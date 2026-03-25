package com.vault.application.usecase;

import com.vault.application.dto.LoginResponse;
import com.vault.application.dto.RefreshTokenRequest;
import com.vault.domain.exception.InvalidTokenException;
import com.vault.domain.model.shared.Expiration;
import com.vault.domain.model.token.RefreshToken;
import com.vault.domain.model.token.TokenHash;
import com.vault.domain.model.token.TokenId;
import com.vault.domain.repository.RefreshTokenRepository;
import com.vault.domain.repository.UserRepository;
import com.vault.infrastructure.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class RefreshTokenUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final long refreshTokenDays;

    public RefreshTokenUseCase(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository,
                               JwtService jwtService,
                               @Value("${app.security.jwt.refresh-token-expiration-days:7}") long refreshTokenDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenDays = refreshTokenDays;
    }

    public LoginResponse execute(RefreshTokenRequest request) {
        var hash = new TokenHash(sha256(request.refreshToken()));
        var existingToken = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(InvalidTokenException::new);

        validateToken(existingToken);
        existingToken.revoke();
        refreshTokenRepository.update(existingToken);

        var user = userRepository.findById(existingToken.toUserId())
                .orElseThrow(InvalidTokenException::new);

        var newRawToken = UUID.randomUUID().toString();
        var newRefreshToken = RefreshToken.create(
                TokenId.generate(), user.toUserId(),
                new TokenHash(sha256(newRawToken)),
                Expiration.of(Instant.now().plusSeconds(refreshTokenDays * 86400)));
        refreshTokenRepository.save(newRefreshToken);

        var accessToken = jwtService.generateToken(user.toUserId(), user.toEmail());
        return LoginResponse.of(accessToken, newRawToken, jwtService.accessTokenExpirationSeconds());
    }

    private void validateToken(RefreshToken token) {
        if (!token.isUsable()) {
            throw new InvalidTokenException();
        }
    }

    private String sha256(String input) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 not available", exception);
        }
    }
}
