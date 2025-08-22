package com.salq.backend.admin.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalaryComponentUpdateRequest {
    private String componentName;
    private String valueType;     // 'Percentage' or 'Fixed'
    private BigDecimal value;
    private LocalDate effectiveFrom;
    private Long userId;
}
