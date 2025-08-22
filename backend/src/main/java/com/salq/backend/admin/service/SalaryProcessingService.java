package com.salq.backend.admin.service;

import com.salq.backend.admin.dto.SalaryProcessRequest;
import com.salq.backend.admin.model.*;
import com.salq.backend.admin.repository.*;
import com.salq.backend.staff.model.Staff;
import com.salq.backend.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
        String payrollPeriod = request.getYear() + "-" + String.format("%02d", request.getMonth());
        LocalDate today = LocalDate.now();

        // Fetch all active salary components
        List<SalaryComponents> activeComponents =
                componentRepo.findByEffectiveFromLessThanEqualAndEffectiveToIsNullOrEffectiveToGreaterThanEqual(today, today);

        for (SalaryProcessRequest.EmployeeSalaryData empData : request.getEmployeeData()) {
            Staff staff = staffRepo.findById(empData.getEmployeeId())
                    .orElseThrow(() -> new RuntimeException("Staff not found: " + empData.getEmployeeId()));

            // Insert into salary_transactions
            SalaryTransaction transaction = new SalaryTransaction();
            transaction.setStaff(staff);
            transaction.setPayrollPeriod(payrollPeriod);
            transaction.setLop(empData.getLop());
            transaction.setIncomeTax(empData.getIncomeTax());
            transaction.setOtherDeductions(empData.getOtherDeductions()); 
            transaction.setFinalised(true);
            

            transaction = transactionRepo.save(transaction);



            BigDecimal basicPay = staff.getBasicPay();

            for (SalaryComponents comp : activeComponents) {
                BigDecimal amount;
                if ("Fixed".equalsIgnoreCase(comp.getValueType())) {
                    amount = comp.getValue();
                } else {
                    amount = basicPay.multiply(comp.getValue()).divide(BigDecimal.valueOf(100));
                }

                SalaryDetail detail = new SalaryDetail();
                detail.setTransaction(transaction);
                detail.setComponentName(comp.getComponentName());
                detail.setComponentType(comp.getComponentType());
                detail.setValueType(comp.getValueType());
                detail.setAmount(amount);
                detailRepo.save(detail);
                
            }

        }
    }
}
