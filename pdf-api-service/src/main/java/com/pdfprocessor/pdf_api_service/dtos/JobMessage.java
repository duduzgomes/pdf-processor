package com.pdfprocessor.pdf_api_service.dtos;

import java.io.Serializable;
import java.util.UUID;

public record JobMessage(
        UUID jobId,
        String fileKey,
        String expectedName,
        String webhookUrl
) implements Serializable {}
