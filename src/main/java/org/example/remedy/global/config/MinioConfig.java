package org.example.remedy.global.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.example.remedy.global.config.properties.MinioProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {
    private final MinioProperties properties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(properties.getMinioUrl())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
    }
}
