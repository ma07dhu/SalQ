package com.salq.backend.hr.controller;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private static final String REPORT_URL = "https://salq.com";

    // Use fully qualified com.lowagie.text.Font to avoid ambiguity with java.awt.Font
    private static final com.lowagie.text.Font TITLE_FONT =
            new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 18, com.lowagie.text.Font.BOLD, Color.BLACK);
    private static final com.lowagie.text.Font SUB_FONT =
            new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.BOLD, Color.DARK_GRAY);
    private static final com.lowagie.text.Font NORMAL_FONT =
            new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.NORMAL, Color.BLACK);
    private static final com.lowagie.text.Font HEADER_FONT_WHITE =
            new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.BOLD, Color.WHITE);
    private static final com.lowagie.text.Font HEADER_FONT_GRAY =
            new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.BOLD, Color.BLACK);

    // ---- Public API ----------------------------------------------------------

    /**
     * Generate 2-page monthly salary report for the selected department.
     * Page 1: Summary (styled table + bar chart + pie chart)
     * Page 2: Employee Wise Breakdown (single table + components explanation)
     */
//    public byte[] generateMonthlySalaryReport(String department) throws Exception {
//        // Build mock dataset once (30–40 employees, Jan last year → Jul this year)
//        List<StaffSalary> allData = createMockData();
//
//        LocalDate now = LocalDate.now();
//        YearMonth previousMonth = YearMonth.from(now.minusMonths(1));
//
//        // Filter by department and keep ONLY previous month rows (one row per employee)
//        List<StaffSalary> filtered = allData.stream()
//                .filter(s -> department == null || department.isBlank()
//                        ? true
//                        : s.getDesignation().equalsIgnoreCase(department))
//                .filter(s -> YearMonth.from(s.getTransactionDate()).equals(previousMonth))
//                .collect(Collectors.toList());
//
//        String monthYear = previousMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
//
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            Document document = new Document(PageSize.A4, 36, 36, 54, 54);
//            PdfWriter writer = PdfWriter.getInstance(document, baos);
//
//            // Attach the fixed HeaderFooterPageEvent
//            writer.setPageEvent(new HeaderFooterPageEvent());
//
//            document.open();
//
//            // PAGE 1 — Summary + Charts
//            addSummaryPage(document, filtered, monthYear, department);
//
//            // PAGE 2 — Employee Wise Breakdown Table
//            document.newPage();
//            addEmployeeBreakdownPage(document, filtered);
//
//            document.close();
//            return baos.toByteArray();
//        }
//    }
    public byte[] generateMonthlySalaryReport(String department) throws Exception {
        // Build mock dataset once (30–40 employees, Jan last year → Jul this year)
        List<StaffSalary> allData = createMockData();

        LocalDate now = LocalDate.now();
        YearMonth previousMonth = YearMonth.from(now.minusMonths(1));

        // ✅ Filter by department: if "All" → take all departments
        List<StaffSalary> filtered = allData.stream()
                .filter(s ->
                        (department == null || department.isBlank() || department.equalsIgnoreCase("All"))
                                ? true
                                : s.getDesignation().equalsIgnoreCase(department)
                )
                .filter(s -> YearMonth.from(s.getTransactionDate()).equals(previousMonth))
                .collect(Collectors.toList());

        String monthYear = previousMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));

        // ✅ Fix department title for "All"
        String deptTitle = (department == null || department.isBlank() || department.equalsIgnoreCase("All"))
                ? "All Departments"
                : department;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 54, 54);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            // PAGE 1 — Summary + Charts
            addSummaryPage(document, filtered, monthYear, deptTitle);

            // PAGE 2 — Employee Wise Breakdown Table
            document.newPage();
            addEmployeeBreakdownPage(document, filtered);

            document.close();
            return baos.toByteArray();
        }
    }



    // ---- Page 1: Summary -----------------------------------------------------

    private void addSummaryPage(Document document,
                                List<StaffSalary> staffList,
                                String monthYear,
                                String department) throws Exception {

        Paragraph title = new Paragraph("Monthly Salary Report - " + monthYear, TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        String deptText = (department == null || department.isBlank())
                ? "All Departments"
                : department;
        Paragraph dept = new Paragraph("Department: " + deptText, SUB_FONT);
        dept.setSpacingAfter(8f);
        document.add(dept);

        // Styled summary table
        PdfPTable summaryTable = new PdfPTable(3);
        summaryTable.setWidths(new float[]{3f, 4f, 4f});
        summaryTable.setWidthPercentage(70f);
        summaryTable.setSpacingBefore(6f);
        summaryTable.setSpacingAfter(12f);

        addHeaderCell(summaryTable, "Total Employees");
        addHeaderCell(summaryTable, "Total Gross Salary");
        addHeaderCell(summaryTable, "Total Net Salary");

        int totalEmployees = staffList.size();
        double totalGross = staffList.stream().mapToDouble(StaffSalary::getGrossSalary).sum();
        double totalNet = staffList.stream().mapToDouble(StaffSalary::getNetSalary).sum();

        addBodyCell(summaryTable, String.valueOf(totalEmployees), Element.ALIGN_CENTER);
        addBodyCell(summaryTable, formatCurrency(totalGross), Element.ALIGN_RIGHT);
        addBodyCell(summaryTable, formatCurrency(totalNet), Element.ALIGN_RIGHT);

        document.add(summaryTable);

        // Bar chart: Gross vs Net per employee
        Image barChartImg = Image.getInstance(createBarChartImage(staffList));
        barChartImg.scaleToFit(520, 260);
        barChartImg.setAlignment(Element.ALIGN_CENTER);
        document.add(barChartImg);

        document.add(Chunk.NEWLINE);

        // Pie chart: Sum of components across selected department employees (previous month)
        Image pieChartImg = Image.getInstance(createPieChartImage(staffList));
        pieChartImg.scaleToFit(520, 260);
        pieChartImg.setAlignment(Element.ALIGN_CENTER);
        document.add(pieChartImg);
    }

    private void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT_WHITE));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(new Color(0, 102, 204));
        cell.setPadding(6f);
        cell.setBorderColor(Color.GRAY);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setHorizontalAlignment(align);
        cell.setPadding(5f);
        cell.setBorderColor(Color.GRAY);
        table.addCell(cell);
    }

    private byte[] createBarChartImage(List<StaffSalary> staffList) throws Exception {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (StaffSalary s : staffList) {
            ds.addValue(s.getGrossSalary(), "Gross", s.getName());
            ds.addValue(s.getNetSalary(), "Net", s.getName());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Gross vs Net Salary", "Employee", "Amount (₹)",
                ds, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, chart, 900, 420);
        return out.toByteArray();
    }

    // ---- Updated Pie Chart Generation (exclude Net Salary) -------------------
    private byte[] createPieChartImage(List<StaffSalary> staffList) throws Exception {
        double totalBasic = staffList.stream().mapToDouble(StaffSalary::getBasicPay).sum();
        double totalDa = staffList.stream().mapToDouble(StaffSalary::getDa).sum();
        double totalHra = staffList.stream().mapToDouble(StaffSalary::getHra).sum();
        double totalAllow = staffList.stream().mapToDouble(StaffSalary::getAllowances).sum();
        double totalPf = staffList.stream().mapToDouble(StaffSalary::getPf).sum();
        double totalEsi = staffList.stream().mapToDouble(StaffSalary::getEsi).sum();
        double totalTax = staffList.stream().mapToDouble(StaffSalary::getPtIt).sum();
        double totalOther = staffList.stream().mapToDouble(StaffSalary::getOtherDeductions).sum();

        DefaultPieDataset ds = new DefaultPieDataset();
        ds.setValue("Basic Pay", totalBasic);
        ds.setValue("DA", totalDa);
        ds.setValue("HRA", totalHra);
        ds.setValue("Allowances", totalAllow);
        ds.setValue("PF", totalPf);
        ds.setValue("ESI", totalEsi);
        ds.setValue("PT/IT", totalTax);
        ds.setValue("Other Deductions", totalOther);

        JFreeChart pie = ChartFactory.createPieChart(
                "Salary Component Distribution", ds, true, true, false);
        pie.setBackgroundPaint(Color.WHITE);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, pie, 900, 420);
        return out.toByteArray();
    }


    // ---- Page 2: Employee Wise Breakdown ------------------------------------

//    private void addEmployeeBreakdownPage(Document doc, List<StaffSalary> staffList) throws DocumentException {
//        Paragraph title = new Paragraph("Employee Wise Breakdown", TITLE_FONT);
//        title.setAlignment(Element.ALIGN_CENTER);
//        title.setSpacingAfter(10f);
//        doc.add(title);
//
//        PdfPTable table = new PdfPTable(14);
//        table.setWidthPercentage(100);
//        table.setWidths(new float[]{1f, 2f, 3f, 2.5f, 2f, 2f, 2f, 2f, 2.5f, 2f, 2f, 2f, 2.2f, 2.5f});
//
//        String[] headers = {
//                "S.No", "Emp ID", "Employee Name", "Designation",
//                "Basic Pay (₹)", "DA (₹)", "HRA (₹)", "Allowances (₹)",
//                "Gross Salary (₹)", "PF (₹)", "ESI (₹)", "PT/IT (₹)",
//                "Other Deductions (₹)", "Net Salary (₹)"
//        };
//        for (String h : headers) {
//            PdfPCell cell = new PdfPCell(new Phrase(h, HEADER_FONT_GRAY));
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setBackgroundColor(Color.LIGHT_GRAY);
//            cell.setPadding(5f);
//            cell.setBorderColor(Color.GRAY);
//            table.addCell(cell);
//        }
//
//        int serial = 1;
//        for (StaffSalary s : staffList) {
//            addRow(table, serial++, s);
//        }
//
//        doc.add(table);
//        doc.add(Chunk.NEWLINE);
//        addSalaryComponentsExplanation(doc);
//    }
private void addEmployeeBreakdownPage(Document doc, List<StaffSalary> staffList) throws DocumentException {
    Paragraph title = new Paragraph("Employee Wise Breakdown", FontFactory.getFont(FontFactory.HELVETICA_BOLD, TITLE_FONT.getSize() - 1));
    title.setAlignment(Element.ALIGN_CENTER);
    title.setSpacingAfter(10f);
    doc.add(title);

    PdfPTable table = new PdfPTable(14);
    table.setWidthPercentage(100);
    table.setWidths(new float[]{1f, 2f, 3.2f, 2.3f, 3f, 2.5f, 2.5f, 2.5f, 2.7f, 2.5f, 2f, 2.3f, 2.2f, 2.7f});

    String[] headers = {
            "S.No", "Emp ID", "Employee Name", "Designation",
            "Basic Pay (₹)", "DA (₹)", "HRA (₹)", "Allowances (₹)",
            "Gross Salary (₹)", "PF (₹)", "ESI (₹)", "PT/IT (₹)",
            "Other Deductions (₹)", "Net Salary (₹)"
    };
    for (String h : headers) {
        PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, HEADER_FONT_GRAY.getSize() - 1)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5f);
        cell.setBorderColor(Color.GRAY);
        table.addCell(cell);
    }

    int serial = 1;
    for (StaffSalary s : staffList) {
        addRow(table, serial++, s);
    }

    doc.add(table);
    doc.add(Chunk.NEWLINE);
    addSalaryComponentsExplanation(doc);
}

    private void addRow(PdfPTable table, int serial, StaffSalary s) {
        table.addCell(makeCell(String.valueOf(serial), Element.ALIGN_CENTER));
        table.addCell(makeCell(String.valueOf(s.getEmpId()), Element.ALIGN_CENTER));
        table.addCell(makeCell(s.getName(), Element.ALIGN_LEFT));
        table.addCell(makeCell(s.getDesignation(), Element.ALIGN_LEFT));

        table.addCell(makeCell(formatCurrency(s.getBasicPay()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getDa()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getHra()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getAllowances()), Element.ALIGN_RIGHT));

        table.addCell(makeCell(formatCurrency(s.getGrossSalary()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getPf()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getEsi()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getPtIt()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getOtherDeductions()), Element.ALIGN_RIGHT));
        table.addCell(makeCell(formatCurrency(s.getNetSalary()), Element.ALIGN_RIGHT));
    }

    private PdfPCell makeCell(String text, int align) {
         final Font SMALL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8);

        PdfPCell c = new PdfPCell(new Phrase(text, SMALL_FONT));
        c.setNoWrap(true);  // prevent breaking into multiple lines

        c.setHorizontalAlignment(align);
        c.setPadding(4f);
        c.setBorderColor(Color.GRAY);
        return c;
    }

    private void addSalaryComponentsExplanation(Document doc) throws DocumentException {
        Paragraph heading = new Paragraph("Salary Components Explained", SUB_FONT);
        heading.setSpacingBefore(8f);
        doc.add(heading);

        Paragraph p = new Paragraph();
        p.setFont(NORMAL_FONT);
        p.add("• Basic Pay: Fixed component of salary.\n");
        p.add("• DA (Dearness Allowance): % of Basic.\n");
        p.add("• HRA (House Rent Allowance): % of Basic.\n");
        p.add("• Allowances: Transport, Medical, Special, etc.\n");
        p.add("• Gross Salary: Basic + DA + HRA + Allowances.\n");
        p.add("• Deductions: PF, ESI, PT/IT, Other deductions.\n");
        p.add("• Net Salary: Gross – Total Deductions.");
        doc.add(p);
    }

    // ---- Header & Footer (timestamp top-left; footer with URL + SalQ) --------

    // ---- Header & Footer (timestamp top-left; footer with URL + SalQ) --------
    private static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private static final com.lowagie.text.Font SMALL_FONT =
                new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL, Color.DARK_GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            // Timestamp with time
            String ts = "Generated: " + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"));

            // Top-left timestamp
            ColumnText.showTextAligned(
                    cb, Element.ALIGN_LEFT,
                    new Phrase(ts, SMALL_FONT),
                    document.left(), document.top() + 10, 0
            );

            // Footer: URL + SalQ, centered
            float x = (document.right() + document.left()) / 2;
            float y = document.bottom() - 10;
            ColumnText.showTextAligned(
                    cb, Element.ALIGN_CENTER,
                    new Phrase(REPORT_URL + "  |  SalQ", SMALL_FONT),
                    x, y, 0
            );
        }
    }


    // ---- Mock Data -----------------------------------------------------------

    /**
     * Creates 30–40 employees spread across departments,
     * with monthly rows from Jan (last year) to Jul (this year).
     * Values are in ₹ and vary by department.
     */
    private List<StaffSalary> createMockData() {
        String[] departments = {"Civil", "CSE", "Mech", "MBA", "Admin", "EE","ECE","T&P","MCA"};
        Random rand = new Random();

        List<StaffSalary> data = new ArrayList<>();
        int empIdCounter = 1001;

        LocalDate start = LocalDate.of(LocalDate.now().minusYears(1).getYear(), Month.JANUARY, 1);
        LocalDate end = LocalDate.of(LocalDate.now().getYear(), Month.JULY, 1);

        for (String dept : departments) {
            int empCount = 5 + rand.nextInt(3); // 5–7 employees per dept
            for (int i = 0; i < empCount; i++) {
                String name = dept.substring(0, Math.min(3, dept.length())).toUpperCase() + " Emp " + (i + 1);

                // Base ranges per department (rough realism)
                double baseMin; double baseMax;
                switch (dept) {
                    case "Civil":
                    case "Mechanical":
                        baseMin = 25000; baseMax = 45000; break;
                    case "Computer Science":
                    case "IT":
                        baseMin = 30000; baseMax = 60000; break;
                    case "Electronics":
                        baseMin = 28000; baseMax = 50000; break;
                    case "MBA":
                        baseMin = 35000; baseMax = 65000; break;
                    default:
                        baseMin = 25000; baseMax = 50000;
                }

                LocalDate cursor = start;
                while (!cursor.isAfter(end)) {
                    double basic = baseMin + rand.nextDouble() * (baseMax - baseMin);

                    StaffSalary s = new StaffSalary();
                    s.setSerialNo(empIdCounter);         // just use empId as serial base for mock
                    s.setEmpId(empIdCounter);
                    s.setName(name);
                    s.setDesignation(dept);
                    s.setBasicPay(round2(basic));

                    // Components as realistic percentages
                    s.setDa(round2(basic * 0.10));               // 10% of basic
                    s.setHra(round2(basic * 0.15));              // 15% of basic
                    s.setAllowances(round2(1500 + rand.nextDouble() * 3500)); // ₹1.5k–5k

                    s.setPf(round2(basic * 0.12));               // 12% PF
                    s.setEsi(round2(300 + rand.nextDouble() * 400)); // ₹300–700
                    s.setPtIt(round2(500 + rand.nextDouble() * 1500)); // ₹500–2k
                    s.setOtherDeductions(round2(rand.nextDouble() * 1000)); // up to ₹1k

                    s.setTransactionDate(cursor.withDayOfMonth(1)); // first of month

                    data.add(s);
                    cursor = cursor.plusMonths(1);
                }
                empIdCounter++;
            }
        }
        return data;
    }

    private static String formatCurrency(double v) {
        return String.format("₹ %,.2f", v);
    }

    private static double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }
}


