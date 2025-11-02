package org.example.remedy.application.storage.port.out;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StoragePort {
    String uploadFile(MultipartFile file);
    String uploadFile(InputStream inputStream, String fileName, String contentType, long contentLength);
}
