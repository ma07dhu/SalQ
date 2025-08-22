package com.salq.backend.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ImportResult {
    private int successCount;
    private int errorCount;
    private List<String> errors;
}
