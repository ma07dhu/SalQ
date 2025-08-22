package com.salq.backend.hr.controller;

import java.time.LocalDate;

public class StaffSalary {

    // Identity
    private int serialNo;
    private int empId;
    private String name;
    // Using "designation" to match your earlier code; store department here
    private String designation;

    // Components
    private double basicPay;
    private double da;
    private double hra;
    private double allowances;

    // Deductions
    private double pf;
    private double esi;
    private double ptIt;
    private double otherDeductions;

    // Transaction date (month row)
    private LocalDate transactionDate;

    // ---- Computed values -----------------------------------------------------

    public double getGrossSalary() {
        return round2(basicPay + da + hra + allowances);
    }

    public double getNetSalary() {
        double deductions = pf + esi + ptIt + otherDeductions;
        return round2(getGrossSalary() - deductions);
    }

    private static double round2(double x) {
        return Math.round(x * 100.0) / 100.0;
    }

    // ---- Getters / Setters ---------------------------------------------------

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getBasicPay() {
        return basicPay;
    }

    public void setBasicPay(double basicPay) {
        this.basicPay = basicPay;
    }

    public double getDa() {
        return da;
    }

    public void setDa(double da) {
        this.da = da;
    }

    public double getHra() {
        return hra;
    }

    public void setHra(double hra) {
        this.hra = hra;
    }

    public double getAllowances() {
        return allowances;
    }

    public void setAllowances(double allowances) {
        this.allowances = allowances;
    }

    public double getPf() {
        return pf;
    }

    public void setPf(double pf) {
        this.pf = pf;
    }

    public double getEsi() {
        return esi;
    }

    public void setEsi(double esi) {
        this.esi = esi;
    }

    public double getPtIt() {
        return ptIt;
    }

    public void setPtIt(double ptIt) {
        this.ptIt = ptIt;
    }

    public double getOtherDeductions() {
        return otherDeductions;
    }

    public void setOtherDeductions(double otherDeductions) {
        this.otherDeductions = otherDeductions;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }
}
