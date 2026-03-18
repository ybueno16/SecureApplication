package com.vault.domain.service;

import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.PasswordHash;

public interface PasswordHashingService {

    PasswordHash hash(MasterPassword password);

    boolean matches(MasterPassword rawPassword, PasswordHash encodedHash);
}
