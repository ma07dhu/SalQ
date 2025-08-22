package com.salq.backend.admin.model;

import com.salq.backend.staff.model.Staff;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "salary_transactions")
public class SalaryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)  // each transaction belongs to one staff
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "payroll_period", length = 7)
    private String payrollPeriod;

    @Column(name = "isFinalised", nullable = false)
    private Boolean isFinalised = true;

    @Column(name = "LOP")
    private Integer lop;

    @Column(name = "Income_Tax", precision = 12, scale = 2)
    private BigDecimal incomeTax;

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Staff getStaff() { return staff; }
    public void setStaff(Staff staff) { this.staff = staff; }

    public String getPayrollPeriod() { return payrollPeriod; }
    public void setPayrollPeriod(String payrollPeriod) { this.payrollPeriod = payrollPeriod; }

    public Boolean getIsFinalised() { return isFinalised; }
    public void setIsFinalised(Boolean isFinalised) { this.isFinalised = isFinalised; }

    public Integer getLop() { return lop; }
    public void setLop(Integer lop) { this.lop = lop; }

    public BigDecimal getIncomeTax() { return incomeTax; }
    public void setIncomeTax(BigDecimal incomeTax) { this.incomeTax = incomeTax; }
}