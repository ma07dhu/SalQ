package com.salq.backend.admin.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class SalaryProcessRequest {
    private int year;
    private int month;
    private List<EmployeeSalaryData> employeeData;

    @Data
    public static class EmployeeSalaryData {
        private Long employeeId;
        private Integer lop;
        private BigDecimal otherDeductions;
        private BigDecimal incomeTax;
    }
}
