package com.salq.backend.admin.repository;

import com.salq.backend.admin.model.SalaryComponents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalaryComponentRepository extends JpaRepository<SalaryComponents, Long> {

    List<SalaryComponents> findByEffectiveFromLessThanEqualAndEffectiveToIsNullOrEffectiveToGreaterThanEqual(
            LocalDate today, LocalDate today2);
}
