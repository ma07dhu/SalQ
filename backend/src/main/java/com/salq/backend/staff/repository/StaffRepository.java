package com.salq.backend.staff.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.salq.backend.staff.model.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByEmail(String email);

    @Query("""
           select distinct s
           from Staff s
           left join fetch s.department d
           left join fetch s.user u
           left join fetch u.roles r
           where s.email = :email
           """)
    Optional<Staff> findByEmailWithJoins(@Param("email") String email);
}
