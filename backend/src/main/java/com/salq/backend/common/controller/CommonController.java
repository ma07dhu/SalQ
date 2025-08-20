package com.salq.backend.common.controller;

import com.salq.backend.common.service.DepartmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommonController {
    
    private final DepartmentService departmentService;

    public CommonController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public List<String> getDepartments() {
        return departmentService.getAllDepartmentNames();
    }
}