package com.pdfprocessor.pdf_api_service.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.pdfprocessor.pdf_api_service.dtos.SubmitDocumentRequest;
import com.pdfprocessor.pdf_api_service.dtos.SubmitDocumentResponse;
import com.pdfprocessor.pdf_api_service.services.DocumentService;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Tag(name = "Documents", description = "Envio de documentos para processamento")
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Enviar documento para processamento",
            description = "Recebe um PDF e inicia o processamento assíncrono via OCR"
    )
    public ResponseEntity<SubmitDocumentResponse> submit(
            @RequestPart("file") MultipartFile file,
            @RequestParam("expectedName") @NotBlank String expectedName,
            @RequestParam("webhookRegistrationId") @NotBlank UUID webhookRegistrationId
    ) {
        SubmitDocumentResponse response = documentService.submit(file,new SubmitDocumentRequest(expectedName, webhookRegistrationId));
        return ResponseEntity.accepted().body(response);
    }
}