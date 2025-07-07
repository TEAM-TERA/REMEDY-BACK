package org.example.remedy.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 못 찾음"));

        return new UserProfileResponse(
                user.getUsername(),
                user.getProfileImage()
        );
    }
}
