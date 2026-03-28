package com.vault.application.usecase;

import com.vault.domain.model.audit.AuditLog;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.AuditLogRepository;
import com.vault.domain.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private AuditLogRepository auditLogRepository;

    private LogoutUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LogoutUseCase(refreshTokenRepository, auditLogRepository);
    }

    @Test
    void shouldRevokeAllTokensForUser() {
        var userId = UserId.generate();
        useCase.execute(userId, "127.0.0.1");
        verify(refreshTokenRepository).revokeAllByUserId(userId);
    }

    @Test
    void shouldCreateAuditLog() {
        var userId = UserId.generate();
        useCase.execute(userId, "192.168.1.1");
        verify(auditLogRepository).save(any(AuditLog.class));
    }
}
