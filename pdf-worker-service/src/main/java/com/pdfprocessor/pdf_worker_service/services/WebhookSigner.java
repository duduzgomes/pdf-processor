package com.pdfprocessor.pdf_worker_service.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class WebhookSigner {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SIGNATURE_VERSION = "v1";

    public WebhookHeaders generateHeaders(String body, String secret) {
        String webhookId = UUID.randomUUID().toString();
        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String signature = sign(webhookId, timestamp, body, secret);

        return new WebhookHeaders(webhookId, timestamp, signature);
    }

    private String sign(String webhookId, String timestamp, String body, String secret) {
        String signedContent = webhookId + "." + timestamp + "." + body;

        try {
            byte[] secretBytes = Base64.getDecoder().decode(secret);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
            byte[] hashBytes = mac.doFinal(
                    signedContent.getBytes(StandardCharsets.UTF_8)
            );

            String signature = Base64.getEncoder().encodeToString(hashBytes);
            return SIGNATURE_VERSION + "," + signature;

        } catch (Exception e) {
            log.error("Erro ao gerar assinatura do webhook: {}", e.getMessage());
            throw new RuntimeException("Falha ao assinar webhook", e);
        }
    }

    public record WebhookHeaders(
            String webhookId,
            String timestamp,
            String signature
    ) {}
}
