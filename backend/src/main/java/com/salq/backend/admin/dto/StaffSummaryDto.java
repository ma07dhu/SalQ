package com.salq.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StaffSummaryDto {
    private Long id;
    private String name;
    private String email;
    private String department;
}
