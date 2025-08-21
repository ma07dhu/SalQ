package com.salq.backend.admin.service;

import com.salq.backend.admin.dto.SalaryComponentUpdateRequest;
import com.salq.backend.admin.model.SalaryComponents;
import com.salq.backend.admin.repository.SalaryComponentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SalaryComponentService {

    @Autowired
    private SalaryComponentRepository repository;

    // GET
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
                    predicate = cb.and(predicate, cb.isNull(root.get("effectiveTo")));
                } else {
                    predicate = cb.and(predicate,
                            cb.equal(root.get("effectiveTo"), LocalDate.parse(effectiveTo)));
                }
            }

            return predicate;
        });
    }

    // POST
    public SalaryComponents createSalaryComponent(SalaryComponents component) {
        return repository.save(component);
    }

    // PUT
    public SalaryComponents updateSalaryComponent(Long id, SalaryComponentUpdateRequest request) {
        return repository.findById(id).map(existing -> {
            if (request.getComponentName() != null) {
                existing.setComponentName(request.getComponentName());
            }
            if (request.getValueType() != null) {
                existing.setValueType(request.getValueType());
            }
            if (request.getValue() != null) {
                existing.setValue(request.getValue());
            }
            if (request.getEffectiveFrom() != null) {
                existing.setEffectiveFrom(request.getEffectiveFrom());
            }
            existing.setUpdatedAt(java.time.LocalDateTime.now());
            return repository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Salary component not found with id " + id));
    }

    // DELETE
    public void deleteSalaryComponent(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Salary component not found with id " + id);
        }
        repository.deleteById(id);
    }
}
