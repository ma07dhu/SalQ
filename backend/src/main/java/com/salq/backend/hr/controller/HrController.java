package com.salq.backend.hr.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:9002")
public class HrController {
    @GetMapping("/api/hr/dashboard")
    public String dashboard() {
        return "Welcome, HR!";
    }
}
