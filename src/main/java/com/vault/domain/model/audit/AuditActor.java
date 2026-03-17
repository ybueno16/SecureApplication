package com.vault.domain.model.audit;

import com.vault.domain.model.shared.IpAddress;
import com.vault.domain.model.user.UserId;

public record AuditActor(UserId userId, IpAddress ipAddress) {
}
