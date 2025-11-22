package org.example.remedy.application.user;

import org.example.remedy.domain.user.application.service.UserService;
import org.example.remedy.domain.user.domain.Status;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(
                "testuser",
                "testtest",
                "test@example.com",
                LocalDate.of(2000, 1, 1),
                true
        );
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void withdrawalUser_Success() {

        // given
        doNothing().when(userRepository).save(any(User.class));

        // when
        userService.withdrawUser(testUser);

        // then
        assertThat(testUser.getStatus()).isEqualTo(Status.WITHDRAWAL);
        assertThat(testUser.getWithdrawalDate()).isNotNull();
    }
}