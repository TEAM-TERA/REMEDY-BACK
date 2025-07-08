package org.example.remedy.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        return new UserProfileResponse(
                user.getUsername(),
                user.getProfileImage()
        );
    }

    @Transactional
    public void updateUserProfile(UserProfileUpdateRequest req, User user) {
        user.updateProfile(req);
        userRepository.save(user);
    }
}
