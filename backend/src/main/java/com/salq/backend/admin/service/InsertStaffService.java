package com.salq.backend.admin.service;

import org.springframework.stereotype.Service;

import com.salq.backend.admin.dto.InsertStaffDTO;
import com.salq.backend.staff.model.Department;
import com.salq.backend.staff.model.Staff;
import com.salq.backend.staff.repository.DepartmentRepository;
import com.salq.backend.staff.repository.StaffRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InsertStaffService {

    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;

    public Staff addStaff(InsertStaffDTO dto) {
        Staff staff = new Staff();

        // link department correctly
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with id " + dto.getDepartmentId()));
        staff.setDepartment(department);

        staff.setName(dto.getName());
        staff.setDesignation(dto.getDesignation());
        staff.setPhone(dto.getPhone());
        staff.setEmail(dto.getEmail());
        staff.setAddress(dto.getAddress());
        staff.setJoiningDate(dto.getJoiningDate());
        staff.setRelievingDate(dto.getRelievingDate());
        staff.setBasicPay(dto.getBasicPay());
        staff.setAnniversaryBonus(dto.getAnniversaryBonus());
        staff.setStatus(dto.getStatus());
        staff.setAccNo(dto.getAccNo());

        return staffRepository.save(staff);
    }
}
