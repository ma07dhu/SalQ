package com.salq.backend.admin.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "salary_components_history")
public class SalaryComponentsHistory {

    @Id
    @SequenceGenerator(name = "salary_components_history_seq", sequenceName = "salary_components_history_history_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "salary_components_history_seq")
    @Column(name = "history_id")
    private Long historyId;

    @Column(name = "component_id", nullable = false)
    private Long componentId;

    @Column(name = "component_name", nullable = false, length = 100)
    private String componentName;

    @Column(name = "component_type", nullable = false, length = 20)
    private String componentType;

    @Column(name = "value_type", nullable = false, length = 20)
    private String valueType;

    @Column(name = "value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

}
