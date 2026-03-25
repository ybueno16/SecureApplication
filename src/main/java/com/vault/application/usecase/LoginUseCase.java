package com.vault.application.usecase;

import com.vault.application.dto.LoginRequest;
import com.vault.application.dto.LoginResponse;
import com.vault.domain.exception.AccountLockedException;
import com.vault.domain.exception.InvalidCredentialsException;
import com.vault.domain.model.audit.AuditAction;
import com.vault.domain.model.audit.AuditLog;
import com.vault.domain.model.shared.Expiration;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.token.RefreshToken;
import com.vault.domain.model.token.TokenHash;
import com.vault.domain.model.token.TokenId;
import com.vault.domain.model.user.Email;
import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.User;
import com.vault.domain.repository.AuditLogRepository;
import com.vault.domain.repository.RefreshTokenRepository;
import com.vault.domain.repository.UserRepository;
import com.vault.domain.service.PasswordHashingService;
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
public class LoginUseCase {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordHashingService passwordHashingService;
    private final JwtService jwtService;
    private final long refreshTokenDays;

    public LoginUseCase(UserRepository userRepository,
                        RefreshTokenRepository refreshTokenRepository,
                        AuditLogRepository auditLogRepository,
                        PasswordHashingService passwordHashingService,
                        JwtService jwtService,
                        @Value("${app.security.jwt.refresh-token-expiration-days:7}") long refreshTokenDays) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordHashingService = passwordHashingService;
        this.jwtService = jwtService;
        this.refreshTokenDays = refreshTokenDays;
    }

    public LoginResponse execute(LoginRequest request, String ipAddressRaw) {
        var email = new Email(request.email());
        var user = findUserOrThrow(email);
        throwIfLocked(user);

        var masterPassword = new MasterPassword(request.masterPassword().toCharArray());
        validatePassword(masterPassword, user);
        masterPassword.clear();

        var refreshTokenRaw = UUID.randomUUID().toString();
        var refreshToken = buildRefreshToken(refreshTokenRaw, user);
        refreshTokenRepository.save(refreshToken);

        auditLogRepository.save(AuditLog.create(
                user.toUserId(), AuditAction.LOGIN, null, new IpAddress(ipAddressRaw)));

        var accessToken = jwtService.generateToken(user.toUserId(), user.toEmail());
        return LoginResponse.of(accessToken, refreshTokenRaw, jwtService.accessTokenExpirationSeconds());
    }

    private User findUserOrThrow(Email email) {
        return userRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);
    }

    private void throwIfLocked(User user) {
        if (user.isLocked()) {
            throw new AccountLockedException();
        }
    }

    private void validatePassword(MasterPassword masterPassword, User user) {
        if (!passwordHashingService.matches(masterPassword, user.toPasswordHash())) {
            user.recordFailedLogin();
            userRepository.update(user);
            throw new InvalidCredentialsException();
        }
        user.resetFailedAttempts();
        userRepository.update(user);
    }

    private RefreshToken buildRefreshToken(String raw, User user) {
        var tokenHash = new TokenHash(sha256(raw));
        var expiration = Expiration.of(Instant.now().plusSeconds(refreshTokenDays * 86400));
        return RefreshToken.create(TokenId.generate(), user.toUserId(), tokenHash, expiration);
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
