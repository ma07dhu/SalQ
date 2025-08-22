package com.salq.backend.admin.repository;

import com.salq.backend.admin.model.SalaryComponentsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryComponentsHistoryRepository extends JpaRepository<SalaryComponentsHistory, Long> {

}
