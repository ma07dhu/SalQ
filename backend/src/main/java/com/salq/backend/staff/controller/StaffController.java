package com.salq.backend.staff.controller;

import java.security.Principal;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salq.backend.staff.dto.StaffProfileDTO;
import com.salq.backend.staff.model.Staff;
import com.salq.backend.staff.repository.StaffRepository;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin(origins = "http://localhost:9002")
public class StaffController {

    @Autowired
    private StaffRepository staffRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Welcome, Staff!";
    }

    @GetMapping("/profile")
    public StaffProfileDTO getProfile(Principal principal) {
        String email = principal.getName();

        // fetch with department + user + roles
        Staff staff = staffRepository.findByEmailWithJoins(email)
                .orElseThrow(() -> new RuntimeException("Staff not found for email: " + email));

        // extract department name safely
        String departmentName = (staff.getDepartment() != null)
                ? staff.getDepartment().getDeptName()
                : null;

        // extract roles from user
        String role = (staff.getUser() != null && staff.getUser().getRoles() != null)
                ? staff.getUser().getRoles().stream()
                        .map(r -> r.getRoleName())
                        .distinct()
                        .collect(Collectors.joining(","))
                : null;

        return StaffProfileDTO.builder()
                .id(staff.getSid())
                .fullName(staff.getName())
                .email(staff.getEmail())
                .phone(staff.getPhone())
                .address(staff.getAddress())
                .designation(staff.getDesignation())
                .department(departmentName)
                .dateOfJoining(staff.getJoiningDate())
                .role(role)
                .build();
    }
}
