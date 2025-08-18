package com.salq.backend.staff.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "http://localhost:9002")
public class StaffController {


    @GetMapping("/dashboard")
    public String dashboard() {
        return "Welcome, Staff!";
    }
}
