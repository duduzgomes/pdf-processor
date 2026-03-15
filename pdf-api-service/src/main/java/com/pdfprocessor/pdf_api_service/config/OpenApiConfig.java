package com.pdfprocessor.pdf_api_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PDF Processor API")
                        .description("API para processamento assíncrono de PDFs com OCR")
                        .version("1.0.0")
                );
    }
}
