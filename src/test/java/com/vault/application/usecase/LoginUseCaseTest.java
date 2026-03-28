package com.vault.application.usecase;

import com.vault.application.dto.LoginRequest;
import com.vault.domain.exception.AccountLockedException;
import com.vault.domain.exception.InvalidCredentialsException;
import com.vault.domain.model.shared.Expiration;
import com.vault.domain.model.user.*;
import com.vault.domain.repository.AuditLogRepository;
import com.vault.domain.repository.RefreshTokenRepository;
import com.vault.domain.repository.UserRepository;
import com.vault.domain.service.PasswordHashingService;
import com.vault.infrastructure.security.JwtService;
import com.vault.infrastructure.security.RsaKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private PasswordHashingService passwordHashingService;

    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        var jwtService = new JwtService(new RsaKeyProvider(), "test", 15);
        useCase = new LoginUseCase(userRepository, refreshTokenRepository,
                auditLogRepository, passwordHashingService, jwtService, 7);
    }

    @Test
    void shouldLoginSuccessfully() {
        var user = createTestUser();
        when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
        when(passwordHashingService.matches(any(), any())).thenReturn(true);

        var response = useCase.execute(new LoginRequest("test@example.com", "valid-password-123"), "127.0.0.1");

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        verify(refreshTokenRepository).save(any());
        verify(auditLogRepository).save(any());
    }

    @Test
    void shouldThrowOnNonexistentEmail() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute(new LoginRequest("none@test.com", "password-12345"), "127.0.0.1"));
    }

    @Test
    void shouldThrowOnWrongPassword() {
        var user = createTestUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordHashingService.matches(any(), any())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> useCase.execute(new LoginRequest("test@example.com", "wrong-password-12"), "127.0.0.1"));
        verify(userRepository).update(any());
    }

    @Test
    void shouldThrowWhenAccountLocked() {
        var user = createLockedUser();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        assertThrows(AccountLockedException.class,
                () -> useCase.execute(new LoginRequest("locked@test.com", "password-12345"), "127.0.0.1"));
    }

    @Test
    void shouldResetAttemptsOnSuccessfulLogin() {
        var user = createTestUser();
        user.recordFailedLogin();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(passwordHashingService.matches(any(), any())).thenReturn(true);

        useCase.execute(new LoginRequest("test@example.com", "valid-password-123"), "127.0.0.1");

        assertEquals(0, user.toFailedLoginAttempts().value());
        verify(userRepository).update(user);
    }

    private User createTestUser() {
        return User.create(UserId.generate(), new Email("test@example.com"),
                new PasswordHash("hashed"), new KdfSalt(new byte[32]));
    }

    private User createLockedUser() {
        var identity = new UserIdentity(UserId.generate(), new Email("locked@test.com"));
        var passwordData = new PasswordData(new PasswordHash("h"), new KdfSalt(new byte[32]));
        var lock = new AccountLock(new FailedLoginAttempts(10), Expiration.of(Instant.now().plusSeconds(900)));
        return new User(identity, new UserSecurity(passwordData, lock));
    }
}
