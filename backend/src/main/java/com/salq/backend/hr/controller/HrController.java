package com.salq.backend.hr.controller;

import java.io.OutputStream;

import com.salq.backend.hr.controller.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@CrossOrigin(origins = "http://localhost:9002")
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

    // ❌ Removed month (kept your method name as it is)
    @GetMapping("/api/hr/reports/pdf")
    public void downloadMonthlyReport(
            @RequestParam(required = false, defaultValue = "all") String department,
            HttpServletResponse response) throws Exception {

        // month removed from service call
        byte[] pdfData = reportService.generateMonthlySalaryReport(department);

        if (pdfData == null || pdfData.length == 0) {
            response.sendError(HttpServletResponse.SC_NO_CONTENT, "PDF generation failed");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"monthly_salary_report.pdf\"");

        response.setContentLength(pdfData.length);

        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }

}
