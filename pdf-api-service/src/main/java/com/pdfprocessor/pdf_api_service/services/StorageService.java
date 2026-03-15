package com.pdfprocessor.pdf_api_service.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) {
        String fileKey = "uploads/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            log.info("Arquivo '{}' enviado para o MinIO com sucesso", fileKey);
            return fileKey;

        } catch (Exception e) {
            log.error("Erro ao fazer upload do arquivo '{}': {}", fileKey, e.getMessage());
            throw new RuntimeException("Falha ao armazenar o arquivo", e);
        }
    }
}
