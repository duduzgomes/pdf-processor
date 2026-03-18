package com.pdfprocessor.pdf_worker_service.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pdfprocessor.pdf_worker_service.entities.ProcessingJob;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProcessingJobRepository extends JpaRepository<ProcessingJob, UUID> {
    @Modifying
    @Query("""
            UPDATE ProcessingJob j
            SET j.status = 'PROCESSING', j.attempts = j.attempts + 1
            WHERE j.id = :id AND j.status = 'PENDING'
            """)
    int claimJob(@Param("id") UUID id);

    @Query("""
            SELECT j FROM ProcessingJob j
            LEFT JOIN FETCH j.webhookRegistration
            WHERE j.id = :id
            """)
    Optional<ProcessingJob> findByIdWithWebhook(@Param("id") UUID id);
}
