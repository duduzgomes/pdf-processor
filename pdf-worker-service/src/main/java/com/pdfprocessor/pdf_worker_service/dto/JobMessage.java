package com.pdfprocessor.pdf_worker_service.dto;

import java.util.UUID;

public record JobMessage(
        UUID jobId,
        String fileKey,
        String expectedName
) {}