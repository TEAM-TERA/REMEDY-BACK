package org.example.remedy.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.dto.response.UserProfileImageResponse;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.global.storage.minio.MinioStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserProfileService {
    private final UserRepository userRepository;
    private final MinioStorageService minioStorageService;

    public UserProfileResponse getMyProfile(User user) {
        return UserProfileResponse.newInstance(user);
    }

    @Transactional
    public void updateUserProfile(UserProfileUpdateRequest req, User user) {
        user.updateProfile(req);
        userRepository.save(user);
    }

    @Transactional
    public UserProfileImageResponse updateUserProfileImage(MultipartFile image, User user) {
        String imageUrl = minioStorageService.upload(image);
        user.updateUserProfileImage(imageUrl);
        userRepository.save(user);
        return UserProfileImageResponse.newInstance(user);
    }
}

