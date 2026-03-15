package com.pdfprocessor.pdf_api_service.messaging;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.pdfprocessor.pdf_api_service.dtos.JobMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routing-key.jobs}")
    private String routingKey;

    public void publish(JobMessage message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            log.info("Job '{}' publicado na fila com sucesso", message.jobId());
        } catch (Exception e) {
            log.error("Erro ao publicar job '{}' na fila: {}", message.jobId(), e.getMessage());
            throw new RuntimeException("Falha ao publicar job na fila", e);
        }
    }
}
