package com.salq.backend.admin.repository;

import com.salq.backend.admin.model.SalaryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryTransactionRepository extends JpaRepository<SalaryTransaction, Long> {

    @Query("SELECT t FROM SalaryTransaction t JOIN FETCH t.staff WHERE t.isFinalised = true AND t.payrollPeriod = :period")
    List<SalaryTransaction> findFinalisedTransactionsWithStaff(@Param("period") String period);

}
