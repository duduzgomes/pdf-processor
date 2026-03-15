package com.pdfprocessor.pdf_api_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SubmitDocumentRequest(

        @NotBlank(message = "O nome esperado não pode estar vazio")
        @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
        String expectedName,

        @NotBlank(message = "A URL do webhook não pode estar vazia")
        @Pattern(
                regexp = "^https?://.*",
                message = "A URL do webhook deve começar com http:// ou https://"
        )
        @Size(max = 2048)
        String webhookUrl
) {}
