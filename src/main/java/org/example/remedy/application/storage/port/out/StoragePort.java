package org.example.remedy.application.storage.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {
    String uploadFile(MultipartFile file);
}
