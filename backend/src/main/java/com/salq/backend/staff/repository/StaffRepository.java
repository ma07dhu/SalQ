package com.salq.backend.staff.repository;

import com.salq.backend.admin.dto.StaffSummaryDto;
import com.salq.backend.staff.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    @Query("""
        SELECT new com.salq.backend.admin.dto.StaffSummaryDto(
            s.id,
            s.name,
            s.email,
            d.deptName
        )
        FROM Staff s
        JOIN s.department d
        WHERE s.joiningDate < :beforeDate
    """)
    List<StaffSummaryDto> findStaffActiveBefore(@Param("beforeDate") LocalDate beforeDate);

}