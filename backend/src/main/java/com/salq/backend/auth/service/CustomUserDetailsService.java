package com.salq.backend.auth.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users = new HashMap<>();

    public CustomUserDetailsService() {
        users.put("admin@example.com",
                User.withUsername("admin@example.com")
                        .password("$2a$10$E3JSlkb/wlUktQsHRCuK0OOu3NU2OMxK0saWUIV5j.5opvzZXFALW")
                        .roles("admin").build());
        users.put("hr@example.com",
                User.withUsername("hr@example.com")
                        .password("$2a$10$N6wjOhfTxls1qd4Ukwr5xeuUhm9eaNityJz1DI0RIKxNf9iY5geIe")
                        .roles("hr").build());
        users.put("employee@example.com",
                User.withUsername("employee@example.com")
                        .password("$2a$10$5JaC1SMCOC3.h1j06kVjvenn8UGzV.cCJ4h7YQ5f2L/2T9K1kYgZq")
                        .roles("employee").build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!users.containsKey(username)) {
            throw new UsernameNotFoundException("User not found");
        }
        return users.get(username);
    }
}
