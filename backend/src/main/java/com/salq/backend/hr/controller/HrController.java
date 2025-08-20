package com.salq.backend.hr.controller;

import java.io.OutputStream;

import com.salq.backend.config.PdfSigningUtil;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class HrController {

    private final ReportService reportService;

    @Autowired
    public HrController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/api/hr/dashboard")
    public String dashboard() {
        return "Welcome, HR!";
    }

    @GetMapping("/api/hr/reports/pdf")
    public void downloadMonthlyReport(
            @RequestParam(required = false, defaultValue = "all") String department,
            @RequestParam(required = false, defaultValue = "false") boolean sign,
            HttpServletResponse response) throws Exception {

        byte[] pdfData = reportService.generateMonthlySalaryReport(department);

        if (pdfData == null || pdfData.length == 0) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "PDF generation failed");
            return;
        }

        // Sign the PDF if requested
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
        String filename = sign ? "signed-monthly_salary_report.pdf" : "monthly_salary_report.pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }

    @GetMapping("/api/hr/reports/pdf/annual")
    public void downloadAnnualReport(
            @RequestParam(required = false, defaultValue = "all") String department,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false, defaultValue = "false") boolean sign,
            HttpServletResponse response) throws Exception {

        // Default year to last year if not provided
        int reportYear = (year == null) ? java.time.LocalDate.now().minusYears(1).getYear() : year;

        byte[] pdfData = reportService.generateAnnualSalaryReport(department, reportYear);

        if (pdfData == null || pdfData.length == 0) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "Annual PDF generation failed");
            return;
        }

        // Sign the PDF if requested
        if (sign) {
            try {
                System.out.println("sign parameter value: " + sign);
                pdfData = PdfSigningUtil.signPdf(pdfData);
            } catch (Exception e) {
                System.err.println("Failed to sign PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }

        response.setContentType("application/pdf");
        String filename = sign ? "signed-annual_salary_report_" + reportYear + ".pdf" : "annual_salary_report_" + reportYear + ".pdf";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }
    @GetMapping("/api/hr/reports/date-range")
    public void downloadDateRangeReport(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false, defaultValue = "all") String department,
            @RequestParam(required = false, defaultValue = "false") boolean sign,
            HttpServletResponse response) throws Exception {

        byte[] pdfData = reportService.generateDateRangeSalaryReport(start, end, department);

        if (pdfData == null || pdfData.length == 0) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "Date range PDF generation failed");
            return;
        }

        // Sign the PDF if requested
        if (sign) {
            try {
                pdfData = PdfSigningUtil.signPdf(pdfData);
            } catch (Exception e) {
                System.err.println("Failed to sign PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }

        response.setContentType("application/pdf");
        String filename = sign
                ? String.format("signed-date_range_report_%s_to_%s.pdf", start, end)
                : String.format("date_range_report_%s_to_%s.pdf", start, end);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }

}
