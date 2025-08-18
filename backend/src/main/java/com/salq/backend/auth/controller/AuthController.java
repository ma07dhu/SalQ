package com.salq.backend.auth.controller;
import com.salq.backend.auth.dto.LoginRequest;
import com.salq.backend.auth.dto.LoginResponse;
import com.salq.backend.auth.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin(origins = "http://localhost:9002")
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        System.out.println("IN post /login route");
        System.out.println(loginRequest);
        LoginResponse response = authService.authenticate(loginRequest);
        System.out.println("Response received");
        return ResponseEntity.ok(response);
    }


}
