package com.pdfprocessor.pdf_api_service.dtos;

import java.util.UUID;

public record SubmitDocumentResponse(
        UUID jobId,
        String status,
        String message
) {}
