package org.example.remedy.domain.user.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.application.mapper.UserMapper;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.application.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.application.dto.response.UserProfileImageResponse;
import org.example.remedy.domain.user.application.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.infrastructure.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final StorageService storageService;

    @Transactional
    public void updateUserProfile(UserProfileUpdateRequest req, User user) {
        user.updateProfile(req.username(), req.birthDate(), req.gender());
        userRepository.save(user);
    }

    public UserProfileResponse getMyProfile(User user) {
        return UserMapper.toUserProfileResponse(user);
    }

    @Transactional
    public UserProfileImageResponse updateUserProfileImage(MultipartFile image, User user) {
        String imageUrl = storageService.uploadFile(image);
        user.updateProfileImage(imageUrl);
        userRepository.save(user);
        return UserMapper.toUserProfileImageResponse(user);
    }

    @Transactional
    public void withdrawUser(User user){
        user.withdrawal();
        userRepository.save(user);
    }
}
