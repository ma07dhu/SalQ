package com.salq.backend.admin.repository;

import com.salq.backend.admin.model.SalaryComponents;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface SalaryComponentRepository extends JpaRepository<SalaryComponents, Long> {
    List<SalaryComponents> findByEffectiveFromLessThanEqualAndEffectiveToIsNullOrEffectiveToGreaterThanEqual(
            LocalDate today, LocalDate today2);
}
