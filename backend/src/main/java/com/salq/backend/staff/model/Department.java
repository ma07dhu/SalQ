package com.salq.backend.staff.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "dept_name", unique = true, nullable = false)
    private String deptName;

    public String getDeptName() {
        return deptName;
    }

    public Long getDeptId() {
        return deptId;
    }
}
