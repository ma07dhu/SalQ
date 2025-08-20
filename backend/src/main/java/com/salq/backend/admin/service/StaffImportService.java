package com.salq.backend.admin.service;

import com.opencsv.CSVReader;
import com.salq.backend.admin.dto.ImportResult;
import com.salq.backend.staff.model.Department;
import com.salq.backend.staff.model.Staff;
import com.salq.backend.staff.repository.DepartmentRepository;
import com.salq.backend.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffImportService {

    private final StaffRepository staffRepository;
    
    private final DepartmentRepository departmentRepository;

    public ImportResult importStaff(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            List<String[]> rows = reader.readAll();

            if (rows.isEmpty()) {
                errors.add("Empty file");
                return new ImportResult(0, errors.size(), errors);
            }

            // Skip header row
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                String deptName = row[1].trim();
                try {
                    Department department = departmentRepository.findByDeptName(deptName)
                            .orElseThrow(() -> new RuntimeException("Department not found: " + deptName));
                    Staff staff = new Staff();
                    staff.setName(row[0].trim());
                    staff.setDepartment(department);
                    staff.setDesignation(row[2].trim());
                    staff.setPhone(row[3].trim());
                    staff.setEmail(row[4].trim());
                    staff.setAddress(row[5].trim());
                    staff.setJoiningDate(LocalDate.parse(row[6].trim()));

                    if (!row[7].trim().isEmpty()) {
                        staff.setRelievingDate(LocalDate.parse(row[7].trim()));
                    }

                    staff.setBasicPay(new BigDecimal(row[8].trim()));
                    staff.setAnniversaryBonus(new BigDecimal(row[9].trim()));
                    staff.setStatus(row[10].trim());
                    staff.setAccNo(row[11].trim());

                    staffRepository.save(staff);
                    successCount++;
                } catch (Exception e) {
                    errors.add("Row " + (i + 1) + " could not be processed: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errors.add("File processing error: " + e.getMessage());
        }

        return new ImportResult(successCount, errors.size(), errors);
    }
}
