package com.pdfprocessor.pdf_api_service.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import com.pdfprocessor.pdf_api_service.entities.ProcessingJob;
import com.pdfprocessor.pdf_api_service.enums.JobStatus;
import com.pdfprocessor.pdf_api_service.enums.WebhookStatus;

public record JobStatusResponse(
        UUID jobId,
        JobStatus status,
        String expectedName,
        Boolean nameFound,
        WebhookStatus webhookStatus,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static JobStatusResponse from(ProcessingJob job) {
        return new JobStatusResponse(
                job.getId(),
                job.getStatus(),
                job.getExpectedName(),
                job.getNameFound(),
                job.getWebhookStatus(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
