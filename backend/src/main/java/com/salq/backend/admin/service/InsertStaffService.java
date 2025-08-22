package com.salq.backend.admin.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.salq.backend.admin.dto.InsertStaffDTO;
import com.salq.backend.auth.model.Role;
import com.salq.backend.auth.model.User;
import com.salq.backend.auth.model.UserRole;
import com.salq.backend.auth.repository.RoleRepository;
import com.salq.backend.auth.repository.UserRepository;
import com.salq.backend.auth.repository.UserRoleRepository;
import com.salq.backend.common.service.MailService;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";

    public Staff addStaff(InsertStaffDTO dto) {
        Staff staff = new Staff();

        // Set staff fields
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

        Staff savedStaff = staffRepository.save(staff);

        if (savedStaff.getEmail() != null && savedStaff.getEmail().endsWith("@gmail.com")) {
            String defaultPassword = generateDefaultPassword(8);

            User user = new User();
            user.setStaff(savedStaff);
            user.setEmail(savedStaff.getEmail());
            user.setPasswordHash(passwordEncoder.encode(defaultPassword));
            user.setCreatedAt(LocalDateTime.now());
            user.setLastPasswordChange(null);

            userRepository.save(user);

            Role employeeRole = roleRepository.findByRoleName("Employee")
                    .orElseThrow(() -> new RuntimeException("Employee role not found"));

            // Create and save userRole with effectiveFrom
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(employeeRole);
            userRole.setEffectiveFrom(LocalDate.now());
            userRoleRepository.save(userRole);

            try {
                mailService.sendWelcomeEmail(savedStaff.getEmail(), savedStaff.getName(), defaultPassword);
            } catch (Exception e) {
                System.err.println("Failed to send welcome email: " + e.getMessage());
            }
        }

        return savedStaff;
    }

    private String generateDefaultPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(PASSWORD_CHARS.length());
            sb.append(PASSWORD_CHARS.charAt(index));
        }
        return sb.toString();
    }
}
