package com.pdfprocessor.pdf_api_service.dtos;

import com.pdfprocessor.pdf_api_service.entities.WebhookRegistration;
import java.time.LocalDateTime;
import java.util.UUID;


public record WebhookRegistrationResponse(
        UUID id,
        String ownerId,
        String url,
        Boolean active,
        LocalDateTime createdAt
) {
    public static WebhookRegistrationResponse from(WebhookRegistration registration) {
        return new WebhookRegistrationResponse(
                registration.getId(),
                registration.getOwnerId(),
                registration.getUrl(),
                registration.getActive(),
                registration.getCreatedAt()
        );
    }
}