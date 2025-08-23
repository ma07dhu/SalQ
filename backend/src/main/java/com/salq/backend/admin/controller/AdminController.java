package com.salq.backend.admin.controller;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salq.backend.admin.model.SalaryComponents;

import com.salq.backend.admin.dto.SalaryProcessRequest;
import com.salq.backend.admin.dto.SalaryComponentUpdateRequest;
import com.salq.backend.admin.dto.StaffSummaryDto;

import com.salq.backend.admin.service.SalaryProcessingService;
import com.salq.backend.admin.service.StaffQueryService; 
import com.salq.backend.admin.service.SalaryComponentService;
import com.salq.backend.admin.service.ReportServices;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.time.YearMonth;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    private SalaryComponentService salaryComponentService;
    private final StaffQueryService staffQueryService;
    private final SalaryProcessingService salaryProcessingService;
    private final ReportServices reportService;  



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

    @GetMapping("/is-staff-active-before")
    public ResponseEntity<List<StaffSummaryDto>> getActiveStaffBefore(
            @RequestParam("beforeDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate beforeDate) {

        List<StaffSummaryDto> staff = staffQueryService.getActiveStaffBefore(beforeDate);
        return ResponseEntity.ok(staff);
    }

    @PostMapping("/salary-transactions/process-monthly-transactions")
    public ResponseEntity<String> processMonthlyTransactions(@RequestBody SalaryProcessRequest request) {
        salaryProcessingService.processMonthlyTransactions(request);
        return ResponseEntity.ok("Processed salaries for " + request.getEmployeeData().size() +
                " employees for " + request.getYear() + "-" + request.getMonth());
    }

    @PostMapping("/reports/generate-monthly")
    public ResponseEntity<byte[]> generateMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) throws Exception {

        YearMonth ym = YearMonth.of(year, month);

        byte[] pdfBytes = reportService.generateMonthlySalaryReport(ym);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=salary-report-" + ym + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
