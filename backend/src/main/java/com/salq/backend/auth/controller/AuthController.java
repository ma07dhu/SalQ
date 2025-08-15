package com.salq.backend.auth.controller;
import com.salq.backend.auth.model.LoginRequest;

import com.salq.backend.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:9002")
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

   @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest authRequest) {
        System.out.println("Login attempt with username: " + authRequest.getUsername());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );
            
            UserDetails user = (UserDetails) authentication.getPrincipal();
            System.out.println("User authenticated: " + user.getUsername());
            System.out.println("User roles: " + user.getAuthorities());
            
            String token = jwtUtil.generateToken(user);
            String role = user.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority().replace("ROLE_", "").toLowerCase())
                    .orElse("user");
                    
            return Map.of("token", token, "role", role);
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        }
    }
}
