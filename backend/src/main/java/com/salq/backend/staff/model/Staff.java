package com.salq.backend.staff.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sid;

    private String name;

    private String dept;

    private String designation;

    private String phone;

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
