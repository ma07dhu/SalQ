package com.salq.backend.admin.model;

import com.salq.backend.staff.model.Staff;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "salary_transactions")
public class SalaryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(name = "payroll_period", nullable = false, length = 7)
    private String payrollPeriod; // format YYYY-MM

    @Column(name = "is_finalised", nullable = false)
    private boolean isFinalised = false;

    @Column(name = "lop")
    private Integer lop;

    @Column(name = "income_tax", precision = 12, scale = 2)
    private BigDecimal incomeTax;
       
    @Column(name = "other_deductions", precision = 12, scale = 2)
    private BigDecimal otherDeductions;

}
