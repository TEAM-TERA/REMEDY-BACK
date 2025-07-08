package org.example.remedy.domain.user.service;

import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.user.type.Provider;
import org.example.remedy.domain.user.type.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    @DisplayName("사용자 프로필 조회 성공")
    void getMyProfile_success() {
        //given
        User user = User.builder()
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
}
