package org.example.remedy.domain.user.service;

import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.user.type.Provider;
import org.example.remedy.domain.user.type.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        User user = createUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        //when
        UserProfileResponse response = userService.getMyProfile(1L);

        //then
        assertThat(response.username()).isEqualTo("sejin");
        assertThat(response.profileImageUrl()).isEqualTo("https://image.com/profile.png");
    }

    @Test
    @DisplayName("사용자 프로필 조회 실패 - 존재하지 않는 사용자")
    void getMyProfile_fail_userNotFound() {
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.getMyProfile(99L))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("사용자 프로필 수정 성공")
    void updateProfile_success() {
        // given
        User user = createUser();

        UserProfileUpdateRequest req = new UserProfileUpdateRequest("newName", false);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // when
        userService.updateUserProfile(req, "test@example.com");

        // then
        assertThat(user.getUsername()).isEqualTo("newName");
        assertThat(user.isGender()).isFalse();
    }

    @Test
    @DisplayName("사용자 프로필 수정 실패 - 존재하지 않는 사용자")
    void updateProfile_fail_userNotFound() {
        // given
        UserProfileUpdateRequest req = new UserProfileUpdateRequest("newName", false);
        when(userRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUserProfile(req, "notfound@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

    private User createUser() {
        return User.builder()
                .userId(1L)
                .username("sejin")
                .email("test@example.com")
                .password("password7777")
                .profileImage("https://image.com/profile.png")
                .birthdate(LocalDate.of(2008, 7, 31))
                .gender(true)
                .role(Role.ROLE_USER)
                .provider(Provider.SELF)
                .build();
    }

}
