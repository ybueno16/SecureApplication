package com.vault.infrastructure.security;

import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.PasswordHash;
import com.vault.domain.service.PasswordHashingService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Argon2HashingService implements PasswordHashingService {

    private final Argon2PasswordEncoder encoder;

    public Argon2HashingService() {
        this.encoder = new Argon2PasswordEncoder(16, 32, 1, 65536, 3);
    }

    @Override
    public PasswordHash hash(MasterPassword password) {
        var rawPassword = new String(password.toCharArray());
        return new PasswordHash(encoder.encode(rawPassword));
    }

    @Override
    public boolean matches(MasterPassword rawPassword, PasswordHash encodedHash) {
        var raw = new String(rawPassword.toCharArray());
        return encoder.matches(raw, encodedHash.value());
    }
}
