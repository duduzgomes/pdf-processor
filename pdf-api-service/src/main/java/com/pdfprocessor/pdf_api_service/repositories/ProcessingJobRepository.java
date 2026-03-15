package com.pdfprocessor.pdf_api_service.repositories;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pdfprocessor.pdf_api_service.entities.ProcessingJob;
import com.pdfprocessor.pdf_api_service.enums.JobStatus;

@Repository
public interface ProcessingJobRepository extends JpaRepository<ProcessingJob, UUID> {

    List<ProcessingJob> findByStatus(JobStatus status);
}
