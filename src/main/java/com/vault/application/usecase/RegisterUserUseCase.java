package com.vault.application.usecase;

import com.vault.application.dto.RegisterUserRequest;
import com.vault.application.dto.RegisterUserResponse;
import com.vault.domain.exception.EmailAlreadyExistsException;
import com.vault.domain.model.user.Email;
import com.vault.domain.model.user.KdfSalt;
import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.User;
import com.vault.domain.model.user.UserId;
import com.vault.domain.repository.UserRepository;
import com.vault.domain.service.PasswordHashingService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHashingService passwordHashingService;
    private final SecureRandom secureRandom;

    public RegisterUserUseCase(UserRepository userRepository,
                               PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
        this.secureRandom = new SecureRandom();
    }

    public RegisterUserResponse execute(RegisterUserRequest request) {
        var email = new Email(request.email());
        validateEmailUniqueness(email);

        var masterPassword = new MasterPassword(request.masterPassword().toCharArray());
        var passwordHash = passwordHashingService.hash(masterPassword);
        var kdfSalt = generateKdfSalt();
        var user = User.create(UserId.generate(), email, passwordHash, kdfSalt);

        masterPassword.clear();
        userRepository.save(user);

        return new RegisterUserResponse(
                user.toUserId().value().toString(),
                user.toEmail().value()
        );
    }

    private void validateEmailUniqueness(Email email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email.value());
        }
    }

    private KdfSalt generateKdfSalt() {
        var salt = new byte[32];
        secureRandom.nextBytes(salt);
        return new KdfSalt(salt);
    }
}
