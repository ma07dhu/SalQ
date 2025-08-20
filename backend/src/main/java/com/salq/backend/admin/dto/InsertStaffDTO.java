package com.salq.backend.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class InsertStaffDTO {

    private Long departmentId;   // to map staff to a department

    private String name;

    private String designation;

    private String phone;

    private String email;

    private String address;

    private LocalDate joiningDate;

    private LocalDate relievingDate;

    private BigDecimal basicPay;

    private BigDecimal anniversaryBonus;

    private String status;

    private String accNo;
}
