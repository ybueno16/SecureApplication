package com.vault.application.usecase;

import com.vault.application.dto.RegisterUserRequest;
import com.vault.domain.exception.EmailAlreadyExistsException;
import com.vault.domain.model.user.Email;
import com.vault.domain.model.user.MasterPassword;
import com.vault.domain.model.user.PasswordHash;
import com.vault.domain.repository.UserRepository;
import com.vault.domain.service.PasswordHashingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHashingService passwordHashingService;

    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegisterUserUseCase(userRepository, passwordHashingService);
    }

    @Test
    void shouldRegisterNewUser() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
        when(passwordHashingService.hash(any(MasterPassword.class))).thenReturn(new PasswordHash("hashed"));

        var response = useCase.execute(new RegisterUserRequest("user@test.com", "my-password-12345"));

        assertNotNull(response.userId());
        assertEquals("user@test.com", response.email());
        verify(userRepository).save(any());
    }

    @Test
    void shouldThrowOnDuplicateEmail() {
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> useCase.execute(new RegisterUserRequest("dup@test.com", "my-password-12345")));
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldHashPasswordBeforeSaving() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHashingService.hash(any(MasterPassword.class))).thenReturn(new PasswordHash("hashed"));

        useCase.execute(new RegisterUserRequest("user@test.com", "my-password-12345"));

        verify(passwordHashingService).hash(any(MasterPassword.class));
    }

    @Test
    void shouldNormalizeEmail() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordHashingService.hash(any())).thenReturn(new PasswordHash("h"));

        var response = useCase.execute(new RegisterUserRequest("USER@TEST.COM", "my-password-12345"));

        assertEquals("user@test.com", response.email());
    }
}
