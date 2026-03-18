package com.pdfprocessor.pdf_api_service.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.pdfprocessor.pdf_api_service.dtos.RegisterWebhookRequest;
import com.pdfprocessor.pdf_api_service.dtos.RegisterWebhookResponse;
import com.pdfprocessor.pdf_api_service.dtos.WebhookRegistrationResponse;
import com.pdfprocessor.pdf_api_service.services.WebhookRegistrationService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
@Tag(name = "Webhooks", description = "Registro e gerenciamento de webhooks")
public class WebhookController {

    private final WebhookRegistrationService webhookRegistrationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar webhook",
            description = "Registra uma URL para receber notificações. O secret é retornado apenas nesta chamada."
    )
    public ResponseEntity<RegisterWebhookResponse> register(@RequestBody @Valid RegisterWebhookRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(webhookRegistrationService.register(request));
    }

    @GetMapping
    @Operation(summary = "Listar webhooks", description = "Lista todos os webhooks ativos de um owner")
    public ResponseEntity<List<WebhookRegistrationResponse>> list(@RequestParam String ownerId) {
        return ResponseEntity.ok(webhookRegistrationService.findByOwner(ownerId));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Desativar webhook")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        webhookRegistrationService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
