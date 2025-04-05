package com.moviescatalog.repository;

import com.moviescatalog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findByUsernameOrEmail(String username, String email);
}
