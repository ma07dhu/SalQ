package com.salq.backend.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salq.backend.auth.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find a role by its name, e.g., "ROLE_STAFF", "ROLE_ADMIN"
    Optional<Role> findByRoleName(String roleName);
}
