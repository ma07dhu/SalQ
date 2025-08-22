package com.salq.backend.staff.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder=true)
@AllArgsConstructor
@NoArgsConstructor
public class StaffProfileDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private String designation;
    private String department;
    private LocalDate dateOfJoining;
    private LocalDate relievingDate;
    private BigDecimal basicPay;
    private BigDecimal anniversaryBonus;
    private String status;
    private String accNo;

    // linked User info
    private String username;
    private String role; // comma-separated if multiple
}
