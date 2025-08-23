package com.salq.backend.admin.service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;
import static org.thymeleaf.util.StringUtils.equalsIgnoreCase;

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
import com.salq.backend.admin.model.SalaryDetail;
import com.salq.backend.admin.model.SalaryTransaction;
import com.salq.backend.admin.repository.SalaryDetailRepository;
import com.salq.backend.admin.repository.SalaryTransactionRepository;
import com.salq.backend.staff.model.Staff;

@Service
public class ReportServices {

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

    private final SalaryTransactionRepository salaryTransactionRepository;
    private final SalaryDetailRepository salaryDetailRepository;

    public ReportServices(SalaryTransactionRepository salaryTransactionRepository,
                         SalaryDetailRepository salaryDetailRepository) {
        this.salaryTransactionRepository = salaryTransactionRepository;
        this.salaryDetailRepository = salaryDetailRepository;
    }

    /**
     * Generates monthly salary report (previous month) for selected department (or all).
     * Same layout/styles as your HR version, but data comes from DB.
     */
    public byte[] generateMonthlySalaryReport(YearMonth ym) throws Exception {
        String period = ym.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String monthYear = ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));

//        List<SalaryTransaction> monthTx = salaryTransactionRepository.findAll().stream()
//                .filter(t -> Boolean.TRUE.equals(t.isFinalised()))
//                .filter(t -> period.equals(t.getPayrollPeriod()))
//                .collect(Collectors.toList());
        List<SalaryTransaction> monthTx = salaryTransactionRepository.findFinalisedTransactionsWithStaff(period);


        List<StaffSalary> staffList = monthTx.stream()
                .map(this::toStaffSalary)
                .collect(Collectors.toList());


        String deptTitle = "All Departments"; // since department param is gone

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 54);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new HeaderFooterPageEvent());

            document.open();
            addSummaryPage(document, staffList, monthYear, deptTitle);
            document.newPage();
            document.add(new Paragraph("Page intentionally left blank.", TITLE_FONT));
            document.newPage();
            addEmployeeBreakdownPage(document, staffList);
            document.close();

            return baos.toByteArray();
        }
    }

    // ---------- DB → DTO mapping & calculations ----------

    private StaffSalary toStaffSalary(SalaryTransaction tx) {
        StaffSalary s = new StaffSalary();
        Staff staff = tx.getStaff();

        s.setEmpId(staff != null ? safeLong(staff.getSid()) : null);
        s.setName(staff != null ? nz(staff.getName()) : "Unknown");
        s.setDesignation(staff != null ? nz(staff.getDesignation()) : "");
        s.setDepartment(extractDeptName(staff));
        s.setTransactionDate(parsePeriodToDate(tx.getPayrollPeriod()));
        s.setLop(tx.getLop() == null ? 0 : tx.getLop());
        s.setIt(toDouble(tx.getIncomeTax()));           // IT separate column
        s.setOtherDeductions(toDouble(tx.getOtherDeductions()));

        // Fetch all details (no finder; load-all then group in-memory is too heavy; better load once)
        // Since we have only the single transaction, filter by reference equality match on transaction id
        List<SalaryDetail> details = salaryDetailRepository.findAll().stream()
                .filter(d -> d.getTransaction() != null && Objects.equals(d.getTransaction().getId(), tx.getId()))
                .collect(Collectors.toList());

        // Sum earnings & deductions; also pick out common named pieces
        double earnings = 0.0;
        double deductionsFromDetails = 0.0;

        for (SalaryDetail d : details) {
            double amt = toDouble(d.getAmount());
            boolean isEarning = equalsIgnoreCase(d.getComponentType(), "Earning");
            boolean isDeduction = equalsIgnoreCase(d.getComponentType(), "Deduction");

            String name = nz(d.getComponentName());
            String norm = normalizeComponent(name);

            if (isEarning) {
                earnings += amt;
                // map common earnings
                if (norm.equals("BASIC")) s.setBasicPay(s.getBasicPay() + amt);
                else if (norm.equals("DA")) s.setDa(s.getDa() + amt);
                else if (norm.equals("HRA")) s.setHra(s.getHra() + amt);
                else s.setAllowances(s.getAllowances() + amt); // all other earnings → allowances bucket
            } else if (isDeduction) {
                deductionsFromDetails += amt;
                // map common deductions
                if (norm.equals("PF")) s.setPf(s.getPf() + amt);
                else if (norm.equals("ESI")) s.setEsi(s.getEsi() + amt);
                else if (isPT(norm)) s.setPt(s.getPt() + amt); // PT separated
                // any other deduction stays part of general deductions (already in total)
            }
        }

        double gross = round2(earnings);
        // Net = gross − (all detail deductions) − IT − Other
        double net = round2(gross - deductionsFromDetails - s.getIt() - s.getOtherDeductions());

        s.setGrossSalary(gross);
        s.setNetSalary(Math.max(0, net));
        return s;
    }

    private String extractDeptName(Staff staff) {
        if (staff == null || staff.getDepartment() == null) return "";
        try {
            Object v = staff.getDepartment().getClass().getMethod("getDeptName").invoke(staff.getDepartment());
            if (v != null) return v.toString();
        } catch (Exception ignore) {}
        try {
            Object v = staff.getDepartment().getClass().getMethod("getName").invoke(staff.getDepartment());
            if (v != null) return v.toString();
        } catch (Exception ignore) {}
        return "";
    }

    private LocalDate parsePeriodToDate(String yyyyMm) {
        try {
            YearMonth ym = YearMonth.parse(yyyyMm);
            return ym.atDay(1);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private boolean isPT(String norm) {
        return norm.equals("PT") || norm.equals("PROFESSIONAL TAX");
    }

    private String normalizeComponent(String name) {
        String n = name.toUpperCase(Locale.ROOT).trim();
        if (n.contains("BASIC")) return "BASIC";
        if (n.equals("DA") || n.contains("DEARNESS")) return "DA";
        if (n.equals("HRA") || n.contains("HOUSE RENT")) return "HRA";
        if (n.equals("PF") || n.contains("PROVIDENT")) return "PF";
        if (n.equals("ESI")) return "ESI";
        if (n.equals("PT") || n.contains("PROFESSIONAL TAX")) return "PT";
        return n;
    }

    private Long safeLong(Long v) { return v == null ? null : v; }
    private double toDouble(BigDecimal v) { return v == null ? 0.0 : v.doubleValue(); }
    private String nz(String s) { return s == null ? "" : s; }

    // ---------- PDF building (same layout as your HR version) ----------

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

        // Pie chart: Sum of components across selected department employees
        Image pieChartImg = Image.getInstance(createPieChartImageFromDB(staffList));
        pieChartImg.scaleToFit(520, 260);
        pieChartImg.setAlignment(Element.ALIGN_CENTER);
        document.add(pieChartImg);
    }

    private void addEmployeeBreakdownPage(Document doc, List<StaffSalary> staffList) throws DocumentException {
        Paragraph title = new Paragraph("Employee Wise Breakdown", FontFactory.getFont(FontFactory.HELVETICA_BOLD, TITLE_FONT.getSize() - 1));
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);
        doc.add(title);

        // We add LOP (days) and split PT and IT — table now has 15 columns
        PdfPTable table = new PdfPTable(15);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2f, 3.2f, 2.3f, 3f, 2.5f, 2.5f, 2.5f, 2.7f, 2.2f, 2.2f, 2.2f, 2.4f, 2.4f, 2.7f});

        String[] headers = {
                "S.No", "Emp ID", "Employee Name", "Designation",
                "Basic Pay (₹)", "DA (₹)", "HRA (₹)", "Allowances (₹)",
                "Gross Salary (₹)", "PF (₹)", "ESI (₹)", "LOP (days)",
                "PT (₹)", "IT (₹)", "Net Salary (₹)"
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
            table.addCell(makeCell(String.valueOf(serial++), Element.ALIGN_CENTER));
            table.addCell(makeCell(s.getEmpId() == null ? "" : String.valueOf(s.getEmpId()), Element.ALIGN_CENTER));
            table.addCell(makeCell(s.getName(), Element.ALIGN_LEFT));
            table.addCell(makeCell(s.getDesignation(), Element.ALIGN_LEFT));
            table.addCell(makeCell(formatCurrency(s.getBasicPay()), Element.ALIGN_RIGHT));
            table.addCell(makeCell(formatCurrency(s.getDa()), Element.ALIGN_RIGHT));
            table.addCell(makeCell(formatCurrency(s.getHra()), Element.ALIGN_RIGHT));
            table.addCell(makeCell(formatCurrency(s.getAllowances()), Element.ALIGN_RIGHT));
            table.addCell(makeCell(formatCurrency(s.getGrossSalary()), Element.ALIGN_RIGHT));
            table.addCell(makeCell(formatCurrency(s.getPf()), Element.ALIGN_RIGHT));
            table.addCell(makeCell(formatCurrency(s.getEsi()), Element.ALIGN_RIGHT));
            table.addCell(makeCell(String.valueOf(s.getLop()), Element.ALIGN_CENTER)); // LOP days
            table.addCell(makeCell(formatCurrency(s.getPt()), Element.ALIGN_RIGHT));   // PT from details
            table.addCell(makeCell(formatCurrency(s.getIt()), Element.ALIGN_RIGHT));   // IT from transaction
            table.addCell(makeCell(formatCurrency(s.getNetSalary()), Element.ALIGN_RIGHT));
        }

        doc.add(table);
        doc.add(Chunk.NEWLINE);
        addSalaryComponentsExplanation(doc);
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
        p.add("• Deductions: PF, ESI, PT, IT, Other deductions.\n");
        p.add("• Net Salary: Gross – Total Deductions.");
        doc.add(p);
    }

    // ---------- Charts (identical style to your HR version) ----------

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

    private byte[] createPieChartImageFromDB(List<StaffSalary> staffList) throws Exception {
        // Aggregate component totals from the built DTOs
        double totalBasic = staffList.stream().mapToDouble(StaffSalary::getBasicPay).sum();
        double totalDa = staffList.stream().mapToDouble(StaffSalary::getDa).sum();
        double totalHra = staffList.stream().mapToDouble(StaffSalary::getHra).sum();
        double totalAllow = staffList.stream().mapToDouble(StaffSalary::getAllowances).sum();
        double totalPf = staffList.stream().mapToDouble(StaffSalary::getPf).sum();
        double totalEsi = staffList.stream().mapToDouble(StaffSalary::getEsi).sum();
        double totalPt = staffList.stream().mapToDouble(StaffSalary::getPt).sum();
        double totalIt = staffList.stream().mapToDouble(StaffSalary::getIt).sum();
        double totalOther = staffList.stream().mapToDouble(StaffSalary::getOtherDeductions).sum();

        DefaultPieDataset ds = new DefaultPieDataset();
        ds.setValue("Basic Pay", totalBasic);
        ds.setValue("DA", totalDa);
        ds.setValue("HRA", totalHra);
        ds.setValue("Allowances", totalAllow);
        ds.setValue("PF", totalPf);
        ds.setValue("ESI", totalEsi);
        ds.setValue("PT", totalPt);
        ds.setValue("IT", totalIt);
        ds.setValue("Other Deductions", totalOther);

        JFreeChart pie = ChartFactory.createPieChart(
                "Salary Component Distribution", ds, true, true, false);
        pie.setBackgroundPaint(Color.WHITE);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ChartUtils.writeChartAsPNG(out, pie, 900, 420);
        return out.toByteArray();
    }

    private static String formatCurrency(double v) {
        return String.format("₹ %,.2f", v);
    }

    private static double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

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

    // ---------- Minimal DTO used only for report rendering ----------

    private static class StaffSalary {
        private Integer serialNo;
        private Long empId;
        private String name;
        private String designation;
        private String department;
        private LocalDate transactionDate;

        private double basicPay;
        private double da;
        private double hra;
        private double allowances;

        private double pf;
        private double esi;
        private double pt; // Professional Tax
        private double it; // Income Tax (from transaction)
        private double otherDeductions;

        private int lop; // days

        private double grossSalary;
        private double netSalary;

        // getters & setters
        public Integer getSerialNo() { return serialNo; }
        public void setSerialNo(Integer serialNo) { this.serialNo = serialNo; }
        public Long getEmpId() { return empId; }
        public void setEmpId(Long empId) { this.empId = empId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public LocalDate getTransactionDate() { return transactionDate; }
        public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
        public double getBasicPay() { return basicPay; }
        public void setBasicPay(double basicPay) { this.basicPay = basicPay; }
        public double getDa() { return da; }
        public void setDa(double da) { this.da = da; }
        public double getHra() { return hra; }
        public void setHra(double hra) { this.hra = hra; }
        public double getAllowances() { return allowances; }
        public void setAllowances(double allowances) { this.allowances = allowances; }
        public double getPf() { return pf; }
        public void setPf(double pf) { this.pf = pf; }
        public double getEsi() { return esi; }
        public void setEsi(double esi) { this.esi = esi; }
        public double getPt() { return pt; }
        public void setPt(double pt) { this.pt = pt; }
        public double getIt() { return it; }
        public void setIt(double it) { this.it = it; }
        public double getOtherDeductions() { return otherDeductions; }
        public void setOtherDeductions(double otherDeductions) { this.otherDeductions = otherDeductions; }
        public int getLop() { return lop; }
        public void setLop(int lop) { this.lop = lop; }
        public double getGrossSalary() { return grossSalary; }
        public void setGrossSalary(double grossSalary) { this.grossSalary = grossSalary; }
        public double getNetSalary() { return netSalary; }
        public void setNetSalary(double netSalary) { this.netSalary = netSalary; }
    }
}
