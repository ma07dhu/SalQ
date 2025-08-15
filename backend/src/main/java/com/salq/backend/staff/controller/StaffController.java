package com.salq.backend.staff.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.salq.backend.staff.service.ImportResult;
import com.salq.backend.staff.service.StaffImportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "http://localhost:9002")
@RequiredArgsConstructor
public class StaffController {

    private final StaffImportService staffImportService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Welcome, Staff!";
    }
    
    @PostMapping("/import")
    public ResponseEntity<ImportResult> importStaff(@RequestParam("file") MultipartFile file) {
        ImportResult result = staffImportService.importStaff(file);
        return ResponseEntity.ok(result);
    }
}
