package com.salq.backend.admin.controller;

import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.salq.backend.config.PdfSigningUtil;
import com.salq.backend.hr.controller.ReportService; // ✅ Reuse same ReportService
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//import com.salq.backend.admin.dto.ImportResult;
import com.salq.backend.admin.service.StaffImportService;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final StaffImportService staffImportService;
    private final ReportService reportService; // ✅ Shared service with HR

    @GetMapping("dashboard")
    public String dashboard() {
        return "Welcome, Admin!";
    }

}