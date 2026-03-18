package com.pdfprocessor.pdf_worker_service.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdfprocessor.pdf_worker_service.dto.WebhookPayload;
import com.pdfprocessor.pdf_worker_service.entities.ProcessingJob;
import com.pdfprocessor.pdf_worker_service.enums.WebhookStatus;
import com.pdfprocessor.pdf_worker_service.repositories.ProcessingJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookDispatcher {

    private final RestClient restClient;
    private final ProcessingJobRepository jobRepository;
    private final WebhookSigner webhookSigner;
    private final ObjectMapper objectMapper;

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 30_000, multiplier = 2.0)
    )
    @Transactional
    public void dispatch(ProcessingJob job) throws Exception {
        String webhookUrl = job.getWebhookRegistration().getUrl();
        String secret = job.getWebhookRegistration().getSecret();

        log.info("Disparando webhook para '{}'", webhookUrl);

        WebhookPayload payload = new WebhookPayload(
                job.getId(),
                job.getStatus().name(),
                job.getNameFound(),
                LocalDateTime.now()
        );

        String body = objectMapper.writeValueAsString(payload);

        WebhookSigner.WebhookHeaders headers = webhookSigner.generateHeaders(body, secret);

        restClient.post()
                .uri(webhookUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("webhook-id", headers.webhookId())
                .header("webhook-timestamp", headers.timestamp())
                .header("webhook-signature", headers.signature())
                .body(body)
                .retrieve()
                .toBodilessEntity();

        job.setWebhookStatus(WebhookStatus.DELIVERED);
        job.setWebhookAttempts(job.getWebhookAttempts() + 1);
        jobRepository.save(job);

        log.info("Webhook entregue com sucesso para '{}'", webhookUrl);
    }

    @Recover
    @Transactional
    public void recover(RestClientException e, ProcessingJob job) {
        log.error("Webhook falhou após todas as tentativas para '{}': {}",
                job.getWebhookRegistration().getUrl(), e.getMessage());

        job.setWebhookStatus(WebhookStatus.FAILED);
        job.setWebhookAttempts(3);
        jobRepository.save(job);
    }
}