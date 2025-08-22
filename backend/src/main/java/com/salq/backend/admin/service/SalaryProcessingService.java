package com.salq.backend.admin.service;

import com.salq.backend.admin.dto.SalaryProcessRequest;
import com.salq.backend.admin.model.SalaryTransaction;
import com.salq.backend.admin.model.SalaryDetail;
import com.salq.backend.salary.model.SalaryComponent;
import com.salq.backend.salary.repository.SalaryTransactionRepository;
import com.salq.backend.salary.repository.SalaryDetailRepository;
import com.salq.backend.salary.repository.SalaryComponentRepository;
import com.salq.backend.staff.repository.StaffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryProcessingService {

    private final SalaryTransactionRepository transactionRepo;
    private final SalaryDetailRepository detailRepo;
    private final SalaryComponentRepository componentRepo;
    private final StaffRepository staffRepo;

    @Transactional
    public void processMonthlyTransactions(SalaryProcessRequest request) {
        LocalDate today = LocalDate.now();
        YearMonth payrollPeriod = YearMonth.of(request.getYear(), request.getMonth());

        // fetch all active components
        List<SalaryComponent> activeComponents = componentRepo.findActiveComponents(today);

        for (SalaryProcessRequest.EmployeeSalaryData empData : request.getEmployeeData()) {
            // 1. Insert into salary_transactions
            SalaryTransaction transaction = new SalaryTransaction();
            transaction.setStaff(staffRepo.findById(empData.getEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid staff ID: " + empData.getEmployeeId())));
            transaction.setPayrollPeriod(payrollPeriod.toString()); // e.g., "2025-08"
            transaction.setLop(empData.getLop());
            transaction.setIncomeTax(BigDecimal.valueOf(empData.getOtherDeductions())); // treat as IncomeTax or extra deduction
            transactionRepo.save(transaction);

            // 2. Compute salary details
            for (SalaryComponent component : activeComponents) {
                BigDecimal baseSalary = transaction.getStaff().getBasicPay();

                BigDecimal amount;
                if (component.getValueType().equalsIgnoreCase("Percentage")) {
                    amount = baseSalary.multiply(component.getValue().divide(BigDecimal.valueOf(100)));
                } else {
                    amount = component.getValue();
                }

                // Apply LOP reduction (simplified: lop days * daily salary)
                if (empData.getLop() > 0) {
                    BigDecimal dailySalary = baseSalary.divide(BigDecimal.valueOf(30), BigDecimal.ROUND_HALF_UP);
                    amount = amount.subtract(dailySalary.multiply(BigDecimal.valueOf(empData.getLop())));
                }

                // Insert into salary_details
                SalaryDetail detail = new SalaryDetail();
                detail.setTransaction(transaction);
                detail.setComponentName(component.getComponentName());
                detail.setComponentType(component.getComponentType());
                detail.setValueType(component.getValueType());
                detail.setAmount(amount.max(BigDecimal.ZERO)); // prevent negative
                detailRepo.save(detail);
            }
        }
    }
}
