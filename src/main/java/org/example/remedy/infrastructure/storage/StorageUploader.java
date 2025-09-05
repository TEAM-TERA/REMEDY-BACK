package org.example.remedy.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageUploader {
    String upload(MultipartFile file);
}
