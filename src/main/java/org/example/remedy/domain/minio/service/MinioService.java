package org.example.remedy.domain.minio.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.example.remedy.global.config.properties.MinioProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String createImageUrl(MultipartFile image) {
        String defaultImage = minioProperties.getMinioUrl() + "/" + minioProperties.getDefaultBucket() + "/image.png";
        if (image == null || image.isEmpty()) {
            return defaultImage;
        }

        return uploadFile(image);
    }

    private String uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getDefaultBucket())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioProperties.getDefaultBucket())
                            .object(fileName)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드에 실패했습니다", e);
        }
    }
}