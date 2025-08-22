package com.salq.backend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.salq.backend.auth.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email, including their roles thanks to EAGER fetch on entity
    Optional<User> findByEmail(String email);
}