package com.salq.backend.staff.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.salq.backend.auth.model.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "staff")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Staff {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;

    @ManyToOne(fetch = FetchType.LAZY)   // Many staff can belong to one department
    @JoinColumn(name = "dept_id", referencedColumnName = "dept_id")
    private Department department;

    @OneToOne(mappedBy = "staff", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    @Column(name = "sname", nullable = false, length = 150)
    private String name;

    private String designation;

    private String phone;

    @Column(unique = true, length = 150)
    private String email;

    private String address;

    @Column(name = "joining_date", nullable = false)
    private LocalDate joiningDate;

    @Column(name = "relieving_date")
    private LocalDate relievingDate;

    @Column(name = "basic_pay")
    private BigDecimal basicPay;

    @Column(name = "anniversary_bonus")
    private BigDecimal anniversaryBonus;

    private String status;

    @Column(name = "acc_no")
    private String accNo;
}
