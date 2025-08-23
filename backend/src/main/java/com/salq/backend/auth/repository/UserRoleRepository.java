package com.salq.backend.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.salq.backend.auth.model.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    // Standard CRUD operations
}
