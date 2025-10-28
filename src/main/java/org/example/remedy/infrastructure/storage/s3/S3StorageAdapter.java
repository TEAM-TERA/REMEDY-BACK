package org.example.remedy.infrastructure.storage.s3;

import lombok.RequiredArgsConstructor;
import org.example.remedy.global.config.properties.S3Properties;
import org.example.remedy.application.storage.port.out.StoragePort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3StorageAdapter implements StoragePort {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public String uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            uploadToS3(inputStream, fileName, file.getContentType(), file.getSize());
            return createImageUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    public String uploadFile(InputStream inputStream, String fileName, String contentType, long contentLength) {
        try {
            uploadToS3(inputStream, fileName, contentType, contentLength);
            return createImageUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private void uploadToS3(InputStream inputStream, String fileName, String contentType, long contentLength) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(s3Properties.getBucket())
                        .key(fileName)
                        .contentType(contentType)
                        .build(),
                RequestBody.fromInputStream(inputStream, contentLength)
        );
    }

    private String createImageUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
            s3Properties.getBucket(),
            s3Properties.getRegion(),
            fileName);
    }
}
