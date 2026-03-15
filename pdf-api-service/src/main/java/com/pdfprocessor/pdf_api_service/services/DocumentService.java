package com.pdfprocessor.pdf_api_service.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.pdfprocessor.pdf_api_service.dtos.JobMessage;
import com.pdfprocessor.pdf_api_service.dtos.JobStatusResponse;
import com.pdfprocessor.pdf_api_service.dtos.SubmitDocumentRequest;
import com.pdfprocessor.pdf_api_service.dtos.SubmitDocumentResponse;
import com.pdfprocessor.pdf_api_service.entities.ProcessingJob;
import com.pdfprocessor.pdf_api_service.exceptions.JobNotFoundException;
import com.pdfprocessor.pdf_api_service.messaging.JobPublisher;
import com.pdfprocessor.pdf_api_service.repositories.ProcessingJobRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final StorageService storageService;
    private final JobPublisher jobPublisher;
    private final ProcessingJobRepository jobRepository;

    @Transactional
    public SubmitDocumentResponse submit(MultipartFile file, SubmitDocumentRequest request) {

        validateFile(file);

        String fileKey = storageService.uploadFile(file);

        log.info("PDF armazenado com chave '{}'", fileKey);

        ProcessingJob job = ProcessingJob.builder()
                .fileKey(fileKey)
                .expectedName(request.expectedName())
                .webhookUrl(request.webhookUrl())
                .build();

        job = jobRepository.save(job);
        log.info("Job '{}' criado com status PENDING", job.getId());

        JobMessage message = new JobMessage(
                job.getId(),
                job.getFileKey(),
                job.getExpectedName(),
                job.getWebhookUrl()
        );

        jobPublisher.publish(message);

        return new SubmitDocumentResponse(
                job.getId(),
                job.getStatus().name(),
                "Documento recebido. Você será notificado via webhook quando o processamento terminar."
        );
    }

    @Transactional(readOnly = true)
    public JobStatusResponse getStatus(UUID jobId) {
        ProcessingJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException(jobId));

        return JobStatusResponse.from(job);
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Somente arquivos PDF são aceitos");
        }

        // 20MB em bytes
        long maxSize = 20L * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("O arquivo não pode ultrapassar 20MB");
        }
    }
}
