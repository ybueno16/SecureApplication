package com.vault.domain.repository;

import com.vault.domain.model.user.Email;
import com.vault.domain.model.user.User;
import com.vault.domain.model.user.UserId;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    void update(User user);

    Optional<User> findByEmail(Email email);

    Optional<User> findById(UserId userId);

    boolean existsByEmail(Email email);
}
