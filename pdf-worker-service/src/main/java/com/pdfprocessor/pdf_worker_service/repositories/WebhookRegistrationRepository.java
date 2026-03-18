package com.pdfprocessor.pdf_worker_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.pdfprocessor.pdf_worker_service.entities.WebhookRegistration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WebhookRegistrationRepository extends JpaRepository<WebhookRegistration, UUID> {

    List<WebhookRegistration> findByOwnerIdAndActiveTrue(String ownerId);

    Optional<WebhookRegistration> findByIdAndActiveTrue(UUID id);
}
