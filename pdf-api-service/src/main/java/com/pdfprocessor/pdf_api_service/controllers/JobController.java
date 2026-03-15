package com.pdfprocessor.pdf_api_service.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pdfprocessor.pdf_api_service.dtos.JobStatusResponse;
import com.pdfprocessor.pdf_api_service.services.DocumentService;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Consulta de status dos jobs de processamento")
public class JobController {

    private final DocumentService documentService;

    @GetMapping("/{id}")
    @Operation(
            summary = "Consultar status do job",
            description = "Retorna o status atual e o resultado do processamento"
    )
    public ResponseEntity<JobStatusResponse> getStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(documentService.getStatus(id));
    }
}
