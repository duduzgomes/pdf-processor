package com.pdfprocessor.pdf_api_service.dtos;

import java.util.UUID;

public record RegisterWebhookResponse(
        UUID webhookId,
        String ownerId,
        String url,
        String secret,
        String message
) {}
