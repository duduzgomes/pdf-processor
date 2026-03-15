package com.pdfprocessor.pdf_api_service.exceptions;

import java.util.UUID;

public class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(UUID jobId) {
        super("Job não encontrado com id: " + jobId);
    }
}
