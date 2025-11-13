package org.example.remedy.application.user.port.in;

import org.example.remedy.domain.user.User;
import org.example.remedy.presentation.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.application.user.dto.response.UserProfileImageResponse;
import org.example.remedy.application.user.dto.response.UserProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserProfileResponse getMyProfile(User user);

    void updateUserProfile(UserProfileUpdateRequest req, User user);

    UserProfileImageResponse updateUserProfileImage(MultipartFile image, User user);

    void withdrawUser(User user);
}
