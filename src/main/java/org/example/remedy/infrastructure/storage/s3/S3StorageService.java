package org.example.remedy.infrastructure.storage.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.infrastructure.storage.exception.FileUploadFailedException;
import org.example.remedy.global.properties.S3Properties;
import org.example.remedy.infrastructure.storage.StorageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @Override
    public String uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            uploadToS3(inputStream, fileName, file.getContentType(), file.getSize());
            return createImageUrl(fileName);
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패: {}", file.getOriginalFilename(), e);
            throw FileUploadFailedException.EXCEPTION;
        }
    }

    public String uploadFile(InputStream inputStream, String fileName, String contentType, long contentLength) {
        try {
            uploadToS3(inputStream, fileName, contentType, contentLength);
            return createImageUrl(fileName);
        } catch (Exception e) {
            log.error("S3 파일 업로드 실패: {}", fileName, e);
            throw FileUploadFailedException.EXCEPTION;
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
        return s3Client.utilities()
                .getUrl(b -> b.bucket(s3Properties.getBucket()).key(fileName))
                .toExternalForm();
    }
}
