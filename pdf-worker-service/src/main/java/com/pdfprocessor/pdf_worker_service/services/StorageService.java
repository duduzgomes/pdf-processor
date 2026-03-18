package com.pdfprocessor.pdf_worker_service.services;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public InputStream downloadFile(String fileKey) {
        try {
            log.info("Baixando arquivo '{}' do MinIO", fileKey);

            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileKey)
                            .build()
            );

            log.info("Arquivo '{}' baixado com sucesso", fileKey);
            return stream;

        } catch (Exception e) {
            log.error("Erro ao baixar arquivo '{}': {}", fileKey, e.getMessage());
            throw new RuntimeException("Falha ao buscar arquivo no MinIO", e);
        }
    }
}
