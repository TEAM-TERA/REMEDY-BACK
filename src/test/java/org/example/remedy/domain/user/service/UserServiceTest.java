package org.example.remedy.domain.user.service;

import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.example.remedy.domain.user.UserTestFactory.create;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자 프로필 조회 성공")
    void getMyProfile_success() {
        //given
        User user = create("sejin", "test@example.com");

        //when
        UserProfileResponse response = userService.getMyProfile(user);

        //then
        assertThat(response.username()).isEqualTo("sejin");
        assertThat(response.profileImageUrl()).isEqualTo(user.getProfileImage());
    }

    @Test
    @DisplayName("사용자 프로필 수정 성공")
    void updateProfile_success() {
        // given
        User user = create("sejin", "test@example.com");

        UserProfileUpdateRequest req = new UserProfileUpdateRequest("newName", Boolean.FALSE,null);

        // when
        userService.updateUserProfile(req, user);

        // then
        assertThat(user.getUsername()).isEqualTo("newName");
        assertThat(user.isGender()).isFalse();
    }

}
