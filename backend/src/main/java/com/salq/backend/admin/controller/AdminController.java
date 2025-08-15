package com.salq.backend.admin.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:9002")
public class AdminController {
    @GetMapping("/api/admin/dashboard")
    public String dashboard() {
        return "Welcome, Admin!";
    }
}
