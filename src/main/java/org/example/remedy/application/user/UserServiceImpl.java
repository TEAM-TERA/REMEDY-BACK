package org.example.remedy.application.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.user.mapper.UserMapper;
import org.example.remedy.application.user.port.in.UserService;
import org.example.remedy.domain.user.User;
import org.example.remedy.presentation.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.application.user.dto.response.UserProfileImageResponse;
import org.example.remedy.application.user.dto.response.UserProfileResponse;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.application.storage.port.out.StoragePort;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserPersistencePort userPersistencePort;
    private final StoragePort storagePort;
    private final TokenProvider tokenProvider;

    @Transactional
    public void updateUserProfile(UserProfileUpdateRequest req, User user) {
        user.updateProfile(req.username(), req.birthDate(), req.gender());
        userPersistencePort.save(user);
    }

    public UserProfileResponse getMyProfile(User user) {
        return UserMapper.toUserProfileResponse(user);
    }

    @Transactional
    public UserProfileImageResponse updateUserProfileImage(MultipartFile image, User user) {
        String imageUrl = storagePort.uploadFile(image);
        user.updateProfileImage(imageUrl);
        userPersistencePort.save(user);
        return UserMapper.toUserProfileImageResponse(user);
    }

    @Transactional
    public void withdrawUser(User user){
        user.withdrawal();
        userPersistencePort.save(user);
    }
}
