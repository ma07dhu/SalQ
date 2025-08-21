package com.salq.backend.admin.controller;

import com.salq.backend.admin.dto.SalaryComponentUpdateRequest;
import com.salq.backend.admin.model.SalaryComponents;
import com.salq.backend.admin.service.SalaryComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SalaryComponentService salaryComponentService;

    // GET with filters

    @GetMapping("dashboard")
    public String greet(){
        return "Welcome, Admin!";
    }

    @GetMapping("salary-components")
    public ResponseEntity<List<SalaryComponents>> getSalaryComponents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String effectiveTo) {
        return ResponseEntity.ok(salaryComponentService.getSalaryComponents(name, type, effectiveTo));
    }

    // POST - create new salary component
    @PostMapping("salary-components")
    public ResponseEntity<SalaryComponents> createSalaryComponent(@RequestBody SalaryComponents component) {
        SalaryComponents saved = salaryComponentService.createSalaryComponent(component);
        return ResponseEntity.ok(saved);
    }

    // PUT - update salary component
    @PutMapping("salary-components/{id}")
    public ResponseEntity<SalaryComponents> updateSalaryComponent(
            @PathVariable Long id,
            @RequestBody SalaryComponentUpdateRequest request) {
                System.out.println("In PUT salary components");
        SalaryComponents updated = salaryComponentService.updateSalaryComponent(id, request);
        return ResponseEntity.ok(updated);
    }


    // DELETE - remove salary component
    @DeleteMapping("salary-components/{id}")
    public ResponseEntity<Void> deleteSalaryComponent(@PathVariable Long id) {
        salaryComponentService.deleteSalaryComponent(id);
        return ResponseEntity.noContent().build();
    }
}
