package com.pdfprocessor.pdf_worker_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookPayload(
        UUID jobId,
        String status,
        Boolean nameFound,
        LocalDateTime processedAt
) {}