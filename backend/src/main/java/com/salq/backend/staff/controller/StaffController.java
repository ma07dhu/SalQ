package com.salq.backend.staff.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:9002")
public class StaffController {
    @GetMapping("/api/staff/dashboard")
    public String dashboard() {
        return "Welcome, Staff!";
    }
}
