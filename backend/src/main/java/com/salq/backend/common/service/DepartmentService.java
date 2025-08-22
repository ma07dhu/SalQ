package com.salq.backend.common.service;

import com.salq.backend.staff.model.Department;
import com.salq.backend.staff.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<String> getAllDepartmentNames() {
        List<Department> departments = departmentRepository.findAll();
        return departments.stream()
                .map(Department::getDeptName)
                .collect(Collectors.toList());
    }
}