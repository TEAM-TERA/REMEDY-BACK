package org.example.remedy.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.minio.service.MinioService;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.dto.response.UserProfileImageResponse;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MinioService minioService;

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
        String imageUrl = minioService.createImageUrl(image);
        user.updateUserProfileImage(imageUrl);
        userRepository.save(user);
        return UserProfileImageResponse.newInstance(user);
    }
}
