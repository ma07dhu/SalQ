package com.salq.backend.auth.controller;
import com.salq.backend.auth.dto.LoginRequest;
import com.salq.backend.auth.dto.LoginResponse;
// import com.salq.backend.auth.model.User;
// import com.salq.backend.auth.repository.UserRepository;
import com.salq.backend.auth.service.AuthService;
// import com.salq.backend.config.JwtUtil;

import jakarta.validation.Valid;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// import java.util.Map;
// import java.util.Optional;

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
        LoginResponse response = authService.authenticate(loginRequest);
        System.out.println("Response received");
        return ResponseEntity.ok(response);
    }


    // private final AuthenticationManager authenticationManager;
    // private final UserRepository userRepository;
    // private final PasswordEncoder passwordEncoder;
    // private final JWTUtil jwtUtil;
    
    // public AuthController(AuthenticationManager authenticationManager,
    //                       UserRepository userRepository,
    //                       PasswordEncoder passwordEncoder,
    //                       JWTUtil jwtUtil) {
    //     this.authenticationManager = authenticationManager;
    //     this.userRepository = userRepository;
    //     this.passwordEncoder = passwordEncoder;
    //     this.jwtUtil = jwtUtil;
    // }

    // @PostMapping("/login")
    // public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        
    //     // 1. Find user by email
    //     Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
    //     if (userOpt.isEmpty()) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                              .body("Invalid email");
    //     }

    //     User user = userOpt.get();

    //     // 2. Validate password
    //     if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //                              .body("Invalid password");
    //     }

    //     // 3. Check role
    //     boolean hasRole = user.getRoles().stream()
    //             .anyMatch(role -> role.getRoleName().equalsIgnoreCase(loginRequest.getRole()));
    //     if (!hasRole) {
    //         return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //                              .body("User does not have access to '" + loginRequest.getRole() + "' resources");
    //     }

    //     // 4. Generate JWT token with user info and roles
    //     String token = jwtUtil.generateToken(user);

    //     // 5. Respond with token and the role requested
    //     LoginResponse response = new LoginResponse(token, loginRequest.getRole());

    //     return ResponseEntity.ok(response);

}
