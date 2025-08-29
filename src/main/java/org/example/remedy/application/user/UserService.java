package org.example.remedy.application.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.User;
import org.example.remedy.interfaces.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.interfaces.user.dto.response.UserProfileImageResponse;
import org.example.remedy.interfaces.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.UserRepository;
import org.example.remedy.global.storage.StorageUploader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final StorageUploader storageUploader;

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
        String imageUrl = storageUploader.upload(image);
        user.updateUserProfileImage(imageUrl);
        userRepository.save(user);
        return UserProfileImageResponse.newInstance(user);
    }
}
