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

//    @PostMapping("/import")
//    public ResponseEntity<ImportResult> importStaff(@RequestParam("file") MultipartFile file) {
//        ImportResult result = staffImportService.importStaff(file);
//        return ResponseEntity.ok(result);
//    }

    // ================== Reports ==================

    // 📌 Monthly Report
    @GetMapping("/reports/pdf")
    public void downloadMonthlyReport(
            @RequestParam(required = false, defaultValue = "all") String department,
            @RequestParam(required = false, defaultValue = "false") boolean sign,
            HttpServletResponse response) throws Exception {

        byte[] pdfData = reportService.generateMonthlySalaryReport(department);

        if (pdfData == null || pdfData.length == 0) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "PDF generation failed");
            return;
        }

        if (sign) {
            try {
                System.out.println("sign parameter value: " + sign);
                pdfData = PdfSigningUtil.signPdf(pdfData);
            } catch (Exception e) {
                // Log error, but return unsigned PDF anyway
                System.err.println("Failed to sign PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"admin_monthly_salary_report.pdf\"");
        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }
    //  Annual Report
    @GetMapping("/reports/pdf/annual")
    public void downloadAnnualReport(
            @RequestParam(required = false, defaultValue = "all") String department,
            @RequestParam(required = false, defaultValue = "false") boolean sign,
            @RequestParam(required = false) Integer year,
            HttpServletResponse response) throws Exception {

        // Default year to current year if not provided
        int reportYear = (year == null) ? LocalDate.now().getYear() : year;

        byte[] pdfData = reportService.generateAnnualSalaryReport(department, reportYear);

        if (pdfData == null || pdfData.length == 0) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "Annual PDF generation failed");
            return;
        }

        if (sign) {
            try {
                System.out.println("sign parameter value: " + sign);
                pdfData = PdfSigningUtil.signPdf(pdfData);
            } catch (Exception e) {
                // Log error, but return unsigned PDF anyway
                System.err.println("Failed to sign PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"admin_annual_salary_report_" + reportYear + ".pdf\"");
        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }


    //  Date Range Report
    //  Date Range Report
    @GetMapping("/reports/pdf/date-range")
    public void downloadDateRangeReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false, defaultValue = "false") boolean sign,
            @RequestParam(required = false, defaultValue = "all") String department,
            HttpServletResponse response) throws Exception {

        byte[] pdfData = reportService.generateDateRangeSalaryReport(startDate, endDate, department);

        if (pdfData == null || pdfData.length == 0) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "PDF generation failed");
            return;
        }

        if (sign) {
            try {
                System.out.println("sign parameter value: " + sign);
                pdfData = PdfSigningUtil.signPdf(pdfData);
            } catch (Exception e) {
                // Log error, but return unsigned PDF anyway
                System.err.println("Failed to sign PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }


        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"admin_date_range_salary_report.pdf\"");
        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }

}