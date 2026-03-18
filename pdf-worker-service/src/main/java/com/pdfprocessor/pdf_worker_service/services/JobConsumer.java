package com.pdfprocessor.pdf_worker_service.services;


import com.pdfprocessor.pdf_worker_service.dto.JobMessage;
import com.pdfprocessor.pdf_worker_service.entities.ProcessingJob;
import com.pdfprocessor.pdf_worker_service.enums.JobStatus;
import com.pdfprocessor.pdf_worker_service.repositories.ProcessingJobRepository;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobConsumer {

    private final ProcessingJobRepository jobRepository;
    private final StorageService storageService;
    private final OcrService ocrService;
    private final WebhookDispatcher webhookDispatcher;

    @RabbitListener(queues = "${rabbitmq.queue.jobs}")
    @Transactional
    public void consume(
            JobMessage message,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag
    ) throws Exception {

        log.info("Mensagem recebida para job '{}'", message.jobId());

        int claimed = jobRepository.claimJob(message.jobId());
        if (claimed == 0) {
            log.info("Job '{}' já foi reclamado por outro worker — ignorando", message.jobId());
            channel.basicAck(deliveryTag, false);
            return;
        }

        ProcessingJob job = jobRepository
                .findByIdWithWebhook(message.jobId())
                .orElseThrow(() -> new RuntimeException(
                        "Job não encontrado: " + message.jobId()));

        try {
            InputStream pdfStream = storageService.downloadFile(message.fileKey());

            boolean nameFound = ocrService.containsName(pdfStream, message.expectedName());

            job.setStatus(nameFound ? JobStatus.DONE : JobStatus.NOT_FOUND);
            job.setNameFound(nameFound);
            jobRepository.save(job);

            log.info("Job '{}' processado — nome {}", message.jobId(),
                    nameFound ? "encontrado" : "não encontrado");

            webhookDispatcher.dispatch(job);

            channel.basicAck(deliveryTag, false);

        } catch (Exception e) {
            log.error("Erro ao processar job '{}': {}", message.jobId(), e.getMessage());

            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            jobRepository.save(job);

            channel.basicNack(deliveryTag, false, false);
        }
    }
}
