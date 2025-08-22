package com.salq.backend.admin.controller;

import java.time.YearMonth;
import com.salq.backend.admin.service.ReportService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.salq.backend.admin.dto.ImportResult;
import com.salq.backend.admin.dto.SalaryProcessRequest;
import com.salq.backend.admin.dto.StaffSummaryDto;
import com.salq.backend.admin.service.SalaryProcessingService;
import com.salq.backend.admin.service.StaffImportService;
import com.salq.backend.admin.service.StaffQueryService;
import com.salq.backend.admin.service.ReportService;   // <-- import your ReportService

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StaffImportService staffImportService;
    private final StaffQueryService staffQueryService;
    private final SalaryProcessingService salaryProcessingService;
    private final ReportService reportService;   // <-- inject report service

    @GetMapping("dashboard")
    public String dashboard() {
        return "Welcome, Admin!";
    }

    @PostMapping("/import")
    public ResponseEntity<ImportResult> importStaff(@RequestParam("file") MultipartFile file) {
        ImportResult result = staffImportService.importStaff(file);
        return ResponseEntity.ok(result);
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

    // ✅ NEW: Monthly Salary Report Download
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
