package com.pdfprocessor.pdf_api_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterWebhookRequest(

        @NotBlank(message = "O owner_id não pode estar vazio")
        @Size(max = 255)
        String ownerId,

        @NotBlank(message = "A URL não pode estar vazia")
        @Pattern(
                regexp = "^https?://.*",
                message = "A URL deve começar com http:// ou https://"
        )
        @Size(max = 2048)
        String url
) {}
