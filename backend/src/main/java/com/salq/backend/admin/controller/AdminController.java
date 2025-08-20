package com.salq.backend.admin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.salq.backend.admin.dto.ImportResult;
import com.salq.backend.admin.dto.InsertStaffDTO;
import com.salq.backend.admin.service.InsertStaffService;
import com.salq.backend.admin.service.StaffImportService;
import com.salq.backend.staff.model.Staff;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StaffImportService staffImportService;
    private final InsertStaffService insertStaffService;

    @GetMapping("dashboard")
    public String dashboard() {
        return "Welcome, Admin!";
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResult> importStaff(@RequestParam("file") MultipartFile file) {
        ImportResult result = staffImportService.importStaff(file);
        return ResponseEntity.ok(result);
    }
     @PostMapping("/staff")
    public ResponseEntity<Staff> addStaff(@RequestBody InsertStaffDTO insertStaffDTO) {
        Staff savedStaff = insertStaffService.addStaff(insertStaffDTO);
        return ResponseEntity.ok(savedStaff);
    }
}
