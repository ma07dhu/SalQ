package com.salq.backend.admin.service;

import com.salq.backend.admin.dto.StaffSummaryDto;
import com.salq.backend.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffQueryService {

    private final StaffRepository staffRepository;

    public List<StaffSummaryDto> getActiveStaffBefore(LocalDate beforeDate) {
        List<StaffSummaryDto> staffList = staffRepository.findStaffActiveBefore(beforeDate);
        System.out.println("THE BEFORE DATE IS : " + beforeDate);
        return staffList.stream()
                .map(s -> new StaffSummaryDto(
                        s.getId(),
                        s.getName(),
                        s.getEmail(),
                        s.getDepartment() != null ? s.getDepartment() : "N/A"
                ))
                .collect(Collectors.toList());
    }
}
