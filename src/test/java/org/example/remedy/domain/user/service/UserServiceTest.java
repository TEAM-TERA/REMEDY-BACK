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

        //when
        UserProfileResponse response = userService.getMyProfile(user);

        //then
        assertThat(response.username()).isEqualTo("sejin");
        assertThat(response.profileImageUrl()).isEqualTo(user.getProfileImage());
    }

    /*@Test
    @DisplayName("사용자 프로필 조회 실패 - 존재하지 않는 사용자")
    void getMyProfile_fail_userNotFound() {
        //given
        User user = createUser();
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> userService.getMyProfile(user))
                .isInstanceOf(UserNotFoundException.class);
    }*/

    @Test
    @DisplayName("사용자 프로필 수정 성공")
    void updateProfile_success() {
        // given
        User user = createUser();

        UserProfileUpdateRequest req = new UserProfileUpdateRequest("newName", Boolean.FALSE,null);

        // when
        userService.updateUserProfile(req, user);

        // then
        assertThat(user.getUsername()).isEqualTo("newName");
        assertThat(user.isGender()).isFalse();
    }

    /*@Test
    @DisplayName("사용자 프로필 수정 실패 - 존재하지 않는 사용자")
    void updateProfile_fail_userNotFound() {
        // given
        User user = createUser();
        UserProfileUpdateRequest req = new UserProfileUpdateRequest("newName", Boolean.FALSE,null);
        when(userRepository.findByEmail("notfound@example.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateUserProfile(req, user))
                .isInstanceOf(UserNotFoundException.class);
    }*/

    private User createUser() {
        return new User(
                "sejin",
                "password7777",
                "test@example.com",
                LocalDate.of(2008, 7, 31),
                true
        );
    }

}
