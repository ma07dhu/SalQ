package com.salq.backend.admin.dto;

import lombok.Data;
import java.util.List;

@Data
public class SalaryProcessRequest {
    private int year;
    private int month;
    private List<EmployeeSalaryData> employeeData;

    @Data
    public static class EmployeeSalaryData {
        private Long employeeId;
        private int lop;
        private double otherDeductions;
    }
}
