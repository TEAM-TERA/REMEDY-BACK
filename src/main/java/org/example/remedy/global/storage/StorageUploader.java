package org.example.remedy.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageUploader {
    String upload(MultipartFile file);
}
