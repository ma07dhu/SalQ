package com.salq.backend.auth.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.salq.backend.auth.dto.LoginRequest;
import com.salq.backend.auth.dto.LoginResponse;
import com.salq.backend.auth.model.User;
import com.salq.backend.auth.repository.UserRepository;
import com.salq.backend.config.JwtUtil;

@Service
public class AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid password");
        }
        String requestedRole = loginRequest.getRole().toUpperCase();
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getRoleName().equalsIgnoreCase(requestedRole));

        
        if (!hasRole) {
            throw new AccessDeniedException("Role not assigned to user");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(token, loginRequest.getRole());
    }
}