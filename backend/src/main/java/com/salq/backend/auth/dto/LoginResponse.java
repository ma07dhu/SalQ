package com.salq.backend.auth.dto;


public class LoginResponse {
    private String token;
    private String role;  // Only the role sent in the request and verified

    // Constructor
    public LoginResponse(String token, String role) {
        this.token = token;
        this.role = role;
    }

    // getters and setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; } 
}
