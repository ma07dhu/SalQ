package com.salq.backend.admin.repository;

import com.salq.backend.admin.model.SalaryComponents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalaryComponentRepository extends JpaRepository<SalaryComponents, Long>, JpaSpecificationExecutor<SalaryComponents> {
}
