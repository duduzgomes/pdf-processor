package com.pdfprocessor.pdf_worker_service.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pdfprocessor.pdf_worker_service.enums.JobStatus;
import com.pdfprocessor.pdf_worker_service.enums.WebhookStatus;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webhook_registration_id")
    private WebhookRegistration webhookRegistration;
}
