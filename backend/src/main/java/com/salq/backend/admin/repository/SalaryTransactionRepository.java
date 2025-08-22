package com.salq.backend.admin.repository;

import com.salq.backend.admin.model.SalaryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryTransactionRepository extends JpaRepository<SalaryTransaction, Long> {}
