package com.salq.backend.admin.service;

import com.salq.backend.admin.model.SalaryComponents;
import com.salq.backend.admin.repository.SalaryComponentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaryComponentService {

    @Autowired
    private SalaryComponentRepository repository;

    public List<SalaryComponents> getSalaryComponents(String name, String type, String effectiveTo) {
        return repository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();

            if (name != null && !name.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("componentName")), "%" + name.toLowerCase() + "%"));
            }

            if (type != null && !type.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.equal(cb.lower(root.get("componentType")), type.toLowerCase()));
            }

            if (effectiveTo != null) {
                if ("null".equalsIgnoreCase(effectiveTo)) {
                    // frontend sends effectiveTo=null → fetch rows where effective_to IS NULL
                    predicate = cb.and(predicate, cb.isNull(root.get("effectiveTo")));
                } else {
                    // if frontend sends an actual date string (yyyy-MM-dd), filter on that
                    predicate = cb.and(predicate,
                            cb.equal(root.get("effectiveTo"), java.time.LocalDate.parse(effectiveTo)));
                }
            }

            return predicate;
        });
    }
}
