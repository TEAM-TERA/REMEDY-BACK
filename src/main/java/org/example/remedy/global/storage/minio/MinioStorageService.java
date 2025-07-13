package org.example.remedy.global.storage.minio;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import org.example.remedy.global.config.properties.MinioProperties;
import org.example.remedy.global.storage.StorageUploader;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MinioStorageService implements StorageUploader {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String upload(MultipartFile image) {
        return uploadFile(image);
    }

    private String uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            putFile(file, fileName, inputStream);
            return createImageUrl(fileName);
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드에 실패했습니다.", e);
        }
    }

    private void putFile(MultipartFile file, String fileName, InputStream inputStream) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, ServerException, XmlParserException {
        // file object 저장
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(minioProperties.getDefaultBucket())
                        .object(fileName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
    }

    private String createImageUrl(String fileName) throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException, NoSuchAlgorithmException, XmlParserException, ServerException {
        // bucket url 생성
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(minioProperties.getDefaultBucket())
                        .object(fileName)
                        .method(Method.GET)
                        .build()
        );
    }
}