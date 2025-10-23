package com.freelance.app.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    private final ApplicationProperties applicationProperties;

    public MinioConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(applicationProperties.getMinio().getUrl())
            .credentials(applicationProperties.getMinio().getAccessKey(), applicationProperties.getMinio().getSecretKey())
            .build();
    }
}
