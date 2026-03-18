package com.pdfprocessor.pdf_worker_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class PdfWorkerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfWorkerServiceApplication.class, args);
	}

}
