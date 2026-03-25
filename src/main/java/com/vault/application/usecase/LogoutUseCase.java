package com.vault.application.usecase;

import com.vault.domain.model.audit.AuditAction;
import com.vault.domain.model.audit.AuditLog;
import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.AuditLogRepository;
import com.vault.domain.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutUseCase {

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditLogRepository auditLogRepository;

    public LogoutUseCase(RefreshTokenRepository refreshTokenRepository,
                         AuditLogRepository auditLogRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public void execute(UserId userId, String ipAddressRaw) {
        refreshTokenRepository.revokeAllByUserId(userId);
        auditLogRepository.save(AuditLog.create(userId, AuditAction.LOGOUT, null, new IpAddress(ipAddressRaw)));
    }
}
