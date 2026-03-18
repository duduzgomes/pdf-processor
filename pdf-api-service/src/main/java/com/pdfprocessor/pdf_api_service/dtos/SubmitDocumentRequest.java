package com.pdfprocessor.pdf_api_service.dtos;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitDocumentRequest(

        @NotBlank(message = "O nome esperado não pode estar vazio")
        @Size(min = 2, max = 255, message = "O nome deve ter entre 2 e 255 caracteres")
        String expectedName,

        @NotNull(message = "O webhook de destino não pode estar vazio")
        UUID webhookRegistrationId
) {}
