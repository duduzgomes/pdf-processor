package com.pdfprocessor.pdf_api_service.exceptions;

import java.util.UUID;

public class WebhookRegistrationNotFoundException extends RuntimeException {

    public WebhookRegistrationNotFoundException(UUID id) {
        super("Webhook registration não encontrado com id: " + id);
    }
}
