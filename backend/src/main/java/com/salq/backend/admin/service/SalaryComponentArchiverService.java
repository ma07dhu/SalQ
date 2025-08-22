package com.salq.backend.admin.service;

import com.salq.backend.admin.repository.SalaryComponentsHistoryRepository;
import com.salq.backend.admin.repository.SalaryComponentRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SalaryComponentArchiverService {

    private static final Logger logger = LoggerFactory.getLogger(SalaryComponentArchiverService.class);

    private final SalaryComponentRepository salaryComponentRepository;
    private final SalaryComponentsHistoryRepository salaryComponentHistoryRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public SalaryComponentArchiverService(SalaryComponentRepository salaryComponentRepository,
                                          SalaryComponentsHistoryRepository salaryComponentHistoryRepository) {
        this.salaryComponentRepository = salaryComponentRepository;
        this.salaryComponentHistoryRepository = salaryComponentHistoryRepository;
    }

    @Scheduled(cron = "*/10 * * * * *") // Runs daily at midnight
    @Transactional
    public void archiveExpiredSalaryComponents() {
        logger.info("Starting archival of expired salary components...");

        try {
            // Step 1: Insert expired components into history table using native query for batch performance
            String insertSql = """
                INSERT INTO salary_components_history 
                (history_id, component_id, component_name, component_type, value_type, effective_from, effective_to, value, archived_at)
                SELECT nextval('salary_components_history_history_id_seq'), component_id, component_name, component_type, value_type, effective_from, effective_to, value, NOW()
                FROM salary_components
                WHERE effective_to IS NOT NULL AND effective_to < CURRENT_DATE
                """;


            Query insertQuery = entityManager.createNativeQuery(insertSql);
            int insertedRows = insertQuery.executeUpdate();
            logger.info("Inserted {} rows into salary_components_history", insertedRows);

//            // Step 2: Delete those archived rows from salary_components
//            String deleteSql = """
//                    DELETE FROM salary_components
//                    WHERE effective_to IS NOT NULL AND effective_to < CURRENT_DATE
//                    """;
//
//            Query deleteQuery = entityManager.createNativeQuery(deleteSql);
//            int deletedRows = deleteQuery.executeUpdate();
//            logger.info("Deleted {} rows from salary_components", deletedRows);

            logger.info("Archival of expired salary components completed successfully.");

        } catch (Exception ex) {
            logger.error("Error occurred while archiving expired salary components", ex);
            // Optional: rethrow or handle according to your needs
            throw ex;
        }
    }
}
