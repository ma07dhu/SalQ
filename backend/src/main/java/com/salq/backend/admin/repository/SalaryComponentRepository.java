package com.salq.backend.admin.repository;

import com.salq.backend.admin.model.SalaryComponents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

public interface SalaryComponentRepository extends JpaRepository<SalaryComponents, Long>, JpaSpecificationExecutor<SalaryComponents> {
    Optional<SalaryComponents> findTopByComponentNameIgnoreCaseAndEffectiveFromLessThanOrderByEffectiveFromDesc(String componentName, LocalDate effectiveFrom);
    List<SalaryComponents> findByEffectiveFromLessThanEqualAndEffectiveToIsNullOrEffectiveToGreaterThanEqual(
        LocalDate today, LocalDate today2);
}
