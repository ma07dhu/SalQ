package com.salq.backend.auth.service;

import com.salq.backend.auth.model.Role;
import com.salq.backend.auth.model.User;
import com.salq.backend.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User appUser = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Set<GrantedAuthority> authorities = appUser.getRoles().stream()
            .map(Role::getRoleName)
            .map(r -> "ROLE_" + r.toUpperCase())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());

        return org.springframework.security.core.userdetails.User
                .withUsername(appUser.getEmail())
                .password(appUser.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}
