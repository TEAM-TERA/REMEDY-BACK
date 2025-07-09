package org.example.remedy.global.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties("minio")
public class MinioProperties {
    private final String minioUrl;
    private final String accessKey;
    private final String secretKey;
    private final String defaultBucket;
}
