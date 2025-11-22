package org.example.remedy.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    String uploadFile(MultipartFile file);
    String uploadFile(InputStream inputStream, String fileName, String contentType, long contentLength);
}
