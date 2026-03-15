package com.pdfprocessor.pdf_api_service.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pdfprocessor.pdf_api_service.enums.JobStatus;
import com.pdfprocessor.pdf_api_service.enums.WebhookStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "processing_jobs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    @Column(name = "file_key", nullable = false, length = 512)
    private String fileKey;

    @Column(name = "expected_name", nullable = false, length = 255)
    private String expectedName;

    @Column(name = "name_found")
    private Boolean nameFound;

    @Column(name = "webhook_url", nullable = false, length = 2048)
    private String webhookUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "webhook_status", nullable = false)
    @Builder.Default
    private WebhookStatus webhookStatus = WebhookStatus.PENDING;

    @Column(name = "webhook_attempts", nullable = false)
    @Builder.Default
    private Integer webhookAttempts = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer attempts = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
