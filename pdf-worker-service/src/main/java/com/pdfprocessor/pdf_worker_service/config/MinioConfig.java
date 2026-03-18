package com.pdfprocessor.pdf_worker_service.config;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        MinioClient client = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();

        verifyBucketExists(client);
        return client;
    }

    private void verifyBucketExists(MinioClient client) {
        try {
            boolean exists = client.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                log.warn("Bucket '{}' não encontrado — a API deve criá-lo primeiro", bucket);
            } else {
                log.info("Bucket '{}' encontrado com sucesso", bucket);
            }
        } catch (Exception e) {
            log.error("Erro ao verificar bucket '{}': {}", bucket, e.getMessage());
            throw new RuntimeException("Falha ao conectar ao MinIO", e);
        }
    }
}
