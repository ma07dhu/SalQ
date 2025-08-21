package com.salq.backend.admin.controller;

import com.salq.backend.admin.dto.ImportResult;
import com.salq.backend.admin.model.SalaryComponents;
import com.salq.backend.admin.service.SalaryComponentService;
import com.salq.backend.admin.service.StaffImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StaffImportService staffImportService;
    private final SalaryComponentService salaryComponentService;

    @GetMapping("dashboard")
    public String dashboard() {
        return "Welcome, Admin!";
    }

    @GetMapping("/salary-components")
    public ResponseEntity<List<SalaryComponents>> getSalaryComponents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String effectiveTo) {

        List<SalaryComponents> components = salaryComponentService.getSalaryComponents(name, type, effectiveTo);
        return ResponseEntity.ok(components);
    }


    @PostMapping("import")
    public ResponseEntity<ImportResult> importStaff(@RequestParam("file") MultipartFile file) {
        ImportResult result = staffImportService.importStaff(file);
        return ResponseEntity.ok(result);
    }
}
