package com.pdfprocessor.pdf_api_service.services;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pdfprocessor.pdf_api_service.dtos.RegisterWebhookRequest;
import com.pdfprocessor.pdf_api_service.dtos.RegisterWebhookResponse;
import com.pdfprocessor.pdf_api_service.dtos.WebhookRegistrationResponse;
import com.pdfprocessor.pdf_api_service.entities.WebhookRegistration;
import com.pdfprocessor.pdf_api_service.exceptions.WebhookRegistrationNotFoundException;
import com.pdfprocessor.pdf_api_service.repositories.WebhookRegistrationRepository;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookRegistrationService {

    private final WebhookRegistrationRepository webhookRegistrationRepository;

    @Transactional
    public RegisterWebhookResponse register(RegisterWebhookRequest request) {
        String secret = generateSecret();

        WebhookRegistration registration = WebhookRegistration.builder()
                .ownerId(request.ownerId())
                .url(request.url())
                .secret(secret)
                .build();

        registration = webhookRegistrationRepository.save(registration);
        log.info("Webhook registrado para owner '{}' com id '{}'",
                request.ownerId(), registration.getId());

        return new RegisterWebhookResponse(
                registration.getId(),
                registration.getOwnerId(),
                registration.getUrl(),
                secret,
                "Webhook registrado. Guarde o secret — ele não será exibido novamente."
        );
    }

    @Transactional(readOnly = true)
    public List<WebhookRegistrationResponse> findByOwner(String ownerId) {
        return webhookRegistrationRepository
                .findByOwnerIdAndActiveTrue(ownerId)
                .stream()
                .map(WebhookRegistrationResponse::from)
                .toList();
    }

    @Transactional
    public void deactivate(UUID id) {
        WebhookRegistration registration = webhookRegistrationRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() -> new WebhookRegistrationNotFoundException(id));

        registration.setActive(false);
        webhookRegistrationRepository.save(registration);
        log.info("Webhook '{}' desativado", id);
    }

    private String generateSecret() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }
}
