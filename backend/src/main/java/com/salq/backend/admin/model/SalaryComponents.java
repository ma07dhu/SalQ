package com.salq.backend.admin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "salary_components")
public class SalaryComponents {

    @Id
    @SequenceGenerator(name = "salary_component_seq", sequenceName = "salary_components_component_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_component_seq")
    @Column(name = "component_id")
    private Long componentId;

    @Column(name = "component_name", nullable = false, length = 100)
    private String componentName;

    @Column(name = "component_type", nullable = false, length = 20)
    private String componentType; // 'Earning' or 'Deduction'

    @Column(name = "value_type", nullable = false, length = 20)
    private String valueType; // 'Percentage' or 'Fixed'

    @Column(name = "value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
