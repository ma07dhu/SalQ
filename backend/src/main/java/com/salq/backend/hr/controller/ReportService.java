package com.salq.backend.hr.controller;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ReportService {

    private static final String REPORT_URL = "https://salq.com";

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

    // ---------- EXISTING MONTHLY REPORT METHOD -----------

    /**
     * Generates monthly salary report for selected department (or all).
     * Existing method - no changes.
     */
    public byte[] generateMonthlySalaryReport(String department) throws Exception {
        List<StaffSalary> allData = createMockData();
        LocalDate now = LocalDate.now();
        YearMonth previousMonth = YearMonth.from(now.minusMonths(1));

        List<StaffSalary> filtered = allData.stream()
                .filter(s -> (department == null || department.isBlank() || department.equalsIgnoreCase("All"))
                        || s.getDesignation().equalsIgnoreCase(department))
                .filter(s -> YearMonth.from(s.getTransactionDate()).equals(previousMonth))
                .collect(Collectors.toList());

        String monthYear = previousMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));
        String deptTitle = (department == null || department.isBlank() || department.equalsIgnoreCase("All")) ? "All Departments" : department;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 54); // Landscape orientation
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            // PAGE 1 — Summary + Charts
            addSummaryPage(document, filtered, monthYear, deptTitle);

            // PAGE 2 — Employee Wise Breakdown Table on new page
            document.newPage();
            Paragraph blank = new Paragraph("Page intentionally left blank.", TITLE_FONT);
            blank.setAlignment(Element.ALIGN_CENTER);
            document.add(blank);
            document.newPage();
            addEmployeeBreakdownPage(document, filtered);


            document.close();

            return baos.toByteArray();
        }
    }


    // ---------- NEW ANNUAL REPORT METHOD -----------

    /**
     * Generates annual salary report PDF for selected department and year.
     * Includes:
     * - Yearly summary
     * - Employee-wise annual aggregates
     * - Bar chart (all departments gross/net)
     * - Pie chart (components for selected department)
     * - Average salary per department
     */
    public byte[] generateAnnualSalaryReport(String department, int year) throws Exception {
        List<StaffSalary> allData = createMockDataAnnual(year);

        List<StaffSalary> filtered = allData.stream()
                .filter(s -> (department == null || department.isBlank() || department.equalsIgnoreCase("All"))
                        || s.getDesignation().equalsIgnoreCase(department))
                .collect(Collectors.toList());

        String yearTitle = String.valueOf(year);
        String deptTitle = (department == null || department.isBlank() || department.equalsIgnoreCase("All")) ? "All Departments" : department;

        // Aggregate data per department for bar chart (always all depts, regardless of filter)
        List<String> allDepartments = getAllDepartments();
        Map<String, Double> grossByDept = new HashMap<>();
        Map<String, Double> netByDept = new HashMap<>();
        for (String dept : allDepartments) {
            double grossSum = allData.stream()
                    .filter(s -> s.getDesignation().equalsIgnoreCase(dept))
                    .mapToDouble(StaffSalary::getGrossSalary).sum();
            double netSum = allData.stream()
                    .filter(s -> s.getDesignation().equalsIgnoreCase(dept))
                    .mapToDouble(StaffSalary::getNetSalary).sum();
            grossByDept.put(dept, grossSum);
            netByDept.put(dept, netSum);
        }

        // Calculate average salary for selected department (or all combined)
        double avgGross = 0.0;
        double avgNet = 0.0;
        int employeeCount = 0;
        if (!filtered.isEmpty()) {
            Map<Integer, StaffSalary> aggregatesPerEmployee = new HashMap<>();
            for (StaffSalary s : filtered) {
                aggregatesPerEmployee.merge(s.getEmpId(), s, (oldVal, newVal) -> {
                    oldVal.setBasicPay(oldVal.getBasicPay() + newVal.getBasicPay());
                    oldVal.setDa(oldVal.getDa() + newVal.getDa());
                    oldVal.setHra(oldVal.getHra() + newVal.getHra());
                    oldVal.setAllowances(oldVal.getAllowances() + newVal.getAllowances());
                    oldVal.setPf(oldVal.getPf() + newVal.getPf());
                    oldVal.setEsi(oldVal.getEsi() + newVal.getEsi());
                    oldVal.setPtIt(oldVal.getPtIt() + newVal.getPtIt());
                    oldVal.setOtherDeductions(oldVal.getOtherDeductions() + newVal.getOtherDeductions());
                    oldVal.setTransactionDate(newVal.getTransactionDate()); // keep latest date
                    return oldVal;
                });
            }
            List<StaffSalary> annualAggregates = new ArrayList<>(aggregatesPerEmployee.values());
            employeeCount = annualAggregates.size();
            avgGross = annualAggregates.stream().mapToDouble(StaffSalary::getGrossSalary).average().orElse(0.0);
            avgNet = annualAggregates.stream().mapToDouble(StaffSalary::getNetSalary).average().orElse(0.0);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 54);
                PdfWriter writer = PdfWriter.getInstance(document, baos);
                writer.setPageEvent(new HeaderFooterPageEvent());

                document.open();

                // Page 1 - Title and Summary Table
                Paragraph title = new Paragraph("Annual Salary Report - " + yearTitle, TITLE_FONT);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                Paragraph deptParagraph = new Paragraph("Department: " + deptTitle, SUB_FONT);
                deptParagraph.setSpacingAfter(12f);
                document.add(deptParagraph);

                // Summary table: Total Employees, Total Gross, Total Net, Average Gross/Net
                PdfPTable summaryTable = new PdfPTable(5);
                summaryTable.setWidths(new float[]{3f, 4f, 4f, 4f, 4f});
                summaryTable.setWidthPercentage(80f);
                summaryTable.setSpacingBefore(6f);
                summaryTable.setSpacingAfter(12f);

                addHeaderCell(summaryTable, "Total Employees");
                addHeaderCell(summaryTable, "Total Gross Salary");
                addHeaderCell(summaryTable, "Total Net Salary");
                addHeaderCell(summaryTable, "Average Gross Salary");
                addHeaderCell(summaryTable, "Average Net Salary");

                double totalGross = annualAggregates.stream().mapToDouble(StaffSalary::getGrossSalary).sum();
                double totalNet = annualAggregates.stream().mapToDouble(StaffSalary::getNetSalary).sum();

                addBodyCell(summaryTable, String.valueOf(employeeCount), Element.ALIGN_CENTER);
                addBodyCell(summaryTable, formatCurrency(totalGross), Element.ALIGN_RIGHT);
                addBodyCell(summaryTable, formatCurrency(totalNet), Element.ALIGN_RIGHT);
                addBodyCell(summaryTable, formatCurrency(avgGross), Element.ALIGN_RIGHT);
                addBodyCell(summaryTable, formatCurrency(avgNet), Element.ALIGN_RIGHT);

                document.add(summaryTable);

                // Bar chart: Aggregate gross/net by department (all departments, always)
                Image barChart = Image.getInstance(createAnnualBarChartImage(grossByDept, netByDept));
                barChart.scaleToFit(700, 350);
                barChart.setAlignment(Element.ALIGN_CENTER);
                document.add(barChart);

                document.add(Chunk.NEWLINE);

                // Pie chart: Components for filtered employees (annual aggregates)
                Image pieChart = Image.getInstance(createPieChartImage(annualAggregates));
                pieChart.scaleToFit(700, 350);
                pieChart.setAlignment(Element.ALIGN_CENTER);
                document.add(pieChart);

                // Page 2 - Employee breakdown (annual aggregated)
                document.newPage();
                addEmployeeBreakdownPage(document, annualAggregates);

                document.close();
                return baos.toByteArray();
            }
        } else {
            // No data matched
            return new byte[0];
        }
    }

    public byte[] generateDateRangeSalaryReport(String startDateStr, String endDateStr, String department) throws Exception {
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        List<StaffSalary> allData = createMockData();

        // Filter by department and date range (inclusive)
        List<StaffSalary> filtered = allData.stream()
                .filter(s -> (department == null || department.isBlank() || department.equalsIgnoreCase("All"))
                        || s.getDesignation().equalsIgnoreCase(department))
                .filter(s -> !s.getTransactionDate().isBefore(startDate) && !s.getTransactionDate().isAfter(endDate))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return new byte[0]; // No data found for range
        }

        String period = startDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " to " +
                endDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 54);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();

            Paragraph title = new Paragraph("Date Range Salary Report - " + period, TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            String deptTitle = (department == null || department.isBlank() || department.equalsIgnoreCase("All"))
                    ? "All Departments"
                    : department;

            Paragraph deptPara = new Paragraph("Department: " + deptTitle, SUB_FONT);
            deptPara.setSpacingAfter(12f);
            document.add(deptPara);

            // Summary table
            PdfPTable summaryTable = new PdfPTable(3);
            summaryTable.setWidths(new float[]{3f, 4f, 4f});
            summaryTable.setWidthPercentage(70f);
            summaryTable.setSpacingBefore(6f);
            summaryTable.setSpacingAfter(12f);

            addHeaderCell(summaryTable, "Total Employees");
            addHeaderCell(summaryTable, "Total Gross Salary");
            addHeaderCell(summaryTable, "Total Net Salary");

            int totalEmployees = (int) filtered.stream().map(StaffSalary::getEmpId).distinct().count();
            double totalGross = filtered.stream().mapToDouble(StaffSalary::getGrossSalary).sum();
            double totalNet = filtered.stream().mapToDouble(StaffSalary::getNetSalary).sum();

            addBodyCell(summaryTable, String.valueOf(totalEmployees), Element.ALIGN_CENTER);
            addBodyCell(summaryTable, formatCurrency(totalGross), Element.ALIGN_RIGHT);
            addBodyCell(summaryTable, formatCurrency(totalNet), Element.ALIGN_RIGHT);

            document.add(summaryTable);

            // Bar chart
            Image barChartImg = Image.getInstance(createBarChartImage(filtered));
            barChartImg.scaleToFit(700, 350);
            barChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(barChartImg);

            document.add(Chunk.NEWLINE);

            // Pie chart
            Image pieChartImg = Image.getInstance(createPieChartImage(filtered));
            pieChartImg.scaleToFit(700, 350);
            pieChartImg.setAlignment(Element.ALIGN_CENTER);
            document.add(pieChartImg);

            // Employee breakdown page
            document.newPage();
            addEmployeeBreakdownPage(document, filtered);

            document.close();

            return baos.toByteArray();
        }
    }


    // ----------- Helper Methods Used by Both Reports -----------------

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
    private void addSummaryPage(Document document, List<StaffSalary> staffList, String monthYear, String department) throws Exception {
        Paragraph title = new Paragraph("Monthly Salary Report - " + monthYear, TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        String deptText = (department == null || department.isBlank()) ? "All Departments" : department;
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
        c.setNoWrap(true);
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

    // --------- Charts -----------

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

    private byte[] createAnnualBarChartImage(Map<String, Double> grossByDept, Map<String, Double> netByDept) throws Exception {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for (String dept : grossByDept.keySet()) {
            ds.addValue(grossByDept.get(dept), "Gross", dept);
            ds.addValue(netByDept.get(dept), "Net", dept);
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Gross vs Net Salary by Department (Annual)", "Department", "Amount (₹)",
                ds, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, chart, 900, 420);
        return out.toByteArray();
    }

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

    // --------- Mock Data ------------

    /**
     * Creates data for 30–40 employees monthly for the past ~1.5 years (monthly records).
     * Used by monthly report (existing).
     */
    private List<StaffSalary> createMockData() {
        String[] departments = {"Civil", "CSE", "Mech", "MBA", "Admin", "EE", "ECE", "T&P", "MCA"};
        Random rand = new Random();
        List<StaffSalary> data = new ArrayList<>();
        int empIdCounter = 1001;
        LocalDate start = LocalDate.of(LocalDate.now().minusYears(1).getYear(), Month.JANUARY, 1);
        LocalDate end = LocalDate.of(LocalDate.now().getYear(), Month.JULY, 1);

        for (String dept : departments) {
            int empCount = 5 + rand.nextInt(3); // 5–7 employees per dept
            for (int i = 0; i < empCount; i++) {
                String name = dept.substring(0, Math.min(3, dept.length())).toUpperCase() + " Emp " + (i + 1);
                double baseMin, baseMax;
                switch (dept.toLowerCase(Locale.ROOT)) {
                    case "civil":
                    case "mechanical":
                        baseMin = 25000;
                        baseMax = 45000;
                        break;
                    case "cse":
                    case "it":
                        baseMin = 30000;
                        baseMax = 60000;
                        break;
                    case "ece":
                    case "electronics":
                        baseMin = 28000;
                        baseMax = 50000;
                        break;
                    case "mba":
                        baseMin = 35000;
                        baseMax = 65000;
                        break;
                    default:
                        baseMin = 25000;
                        baseMax = 50000;
                }

                LocalDate cursor = start;
                while (!cursor.isAfter(end)) {
                    double basic = baseMin + rand.nextDouble() * (baseMax - baseMin);
                    StaffSalary s = new StaffSalary();
                    s.setSerialNo(empIdCounter);
                    s.setEmpId(empIdCounter);
                    s.setName(name);
                    s.setDesignation(dept);
                    s.setBasicPay(round2(basic));
                    s.setDa(round2(basic * 0.10));
                    s.setHra(round2(basic * 0.15));
                    s.setAllowances(round2(1500 + rand.nextDouble() * 3500));
                    s.setPf(round2(basic * 0.12));
                    s.setEsi(round2(300 + rand.nextDouble() * 400));
                    s.setPtIt(round2(500 + rand.nextDouble() * 1500));
                    s.setOtherDeductions(round2(rand.nextDouble() * 1000));
                    s.setTransactionDate(cursor.withDayOfMonth(1));
                    data.add(s);
                    cursor = cursor.plusMonths(1);
                }
                empIdCounter++;
            }
        }
        return data;
    }

    /**
     * Creates yearly aggregated mock data for annual report generation,
     * each employee has one record summing monthly values for the year.
     * Employee names are shortened as requested.
     */
    private List<StaffSalary> createMockDataAnnual(int year) {
        String[] departments = {"Civil", "CSE", "Mech", "MBA", "Admin", "EE", "ECE", "T&P", "MCA"};
        Random rand = new Random();
        List<StaffSalary> data = new ArrayList<>();
        int empIdCounter = 1001;
        // For annual, generate one record per employee summing monthly amounts for the year

        for (String dept : departments) {
            int empCount = 5 + rand.nextInt(3); // 5–7 employees per dept
            for (int i = 0; i < empCount; i++) {
                String name = dept.substring(0, Math.min(3, dept.length())).toUpperCase() + i; // shorter name

                double baseMin, baseMax;
                switch (dept.toLowerCase(Locale.ROOT)) {
                    case "civil":
                    case "mechanical":
                        baseMin = 25000 * 12;
                        baseMax = 45000 * 12;
                        break;
                    case "cse":
                    case "it":
                        baseMin = 30000 * 12;
                        baseMax = 60000 * 12;
                        break;
                    case "ece":
                    case "electronics":
                        baseMin = 28000 * 12;
                        baseMax = 50000 * 12;
                        break;
                    case "mba":
                        baseMin = 35000 * 12;
                        baseMax = 65000 * 12;
                        break;
                    default:
                        baseMin = 25000 * 12;
                        baseMax = 50000 * 12;
                }

                double basicYearly = baseMin + rand.nextDouble() * (baseMax - baseMin);
                StaffSalary s = new StaffSalary();
                s.setSerialNo(empIdCounter);
                s.setEmpId(empIdCounter);
                s.setName(name);
                s.setDesignation(dept);
                s.setBasicPay(round2(basicYearly));
                s.setDa(round2(basicYearly * 0.10));
                s.setHra(round2(basicYearly * 0.15));
                s.setAllowances(round2(1500 * 12 + rand.nextDouble() * 3500 * 12));
                s.setPf(round2(basicYearly * 0.12));
                s.setEsi(round2(300 * 12 + rand.nextDouble() * 400 * 12));
                s.setPtIt(round2(500 * 12 + rand.nextDouble() * 1500 * 12));
                s.setOtherDeductions(round2(rand.nextDouble() * 1000 * 12));
                s.setTransactionDate(LocalDate.of(year, Month.DECEMBER, 31)); // Assign to year-end date
                data.add(s);
                empIdCounter++;
            }
        }
        return data;
    }

    private List<String> getAllDepartments() {
        return Arrays.asList("Civil", "CSE", "Mech", "MBA", "Admin", "EE", "ECE", "T&P", "MCA");
    }

    private static String formatCurrency(double v) {
        return String.format("₹ %,.2f", v);
    }

    private static double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }


    // --------- Header & Footer Event for PDF ------------

    private static class HeaderFooterPageEvent extends PdfPageEventHelper {
        private static final com.lowagie.text.Font SMALL_FONT =
                new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL, Color.DARK_GRAY);

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();

            String ts = "Generated: " + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss"));

            // Timestamp top-left
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(ts, SMALL_FONT),
                    document.left(), document.top() + 10, 0);

            // Footer: URL + SalQ centered
            float x = (document.right() + document.left()) / 2;
            float y = document.bottom() - 10;

            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase(REPORT_URL + " | SalQ", SMALL_FONT),
                    x, y, 0);
        }
    }
}
