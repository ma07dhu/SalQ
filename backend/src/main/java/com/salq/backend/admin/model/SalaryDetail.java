package com.salq.backend.admin.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "salary_details")
public class SalaryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private SalaryTransaction transaction;

    @Column(name = "component_name", nullable = false, length = 100)
    private String componentName;

    @Column(name = "component_type", nullable = false, length = 20)
    private String componentType; // Earning / Deduction

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "value_type", length = 20)
    private String valueType; // Percentage / Fixed

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
