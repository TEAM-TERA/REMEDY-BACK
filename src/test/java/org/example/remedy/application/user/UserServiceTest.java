package org.example.remedy.application.user;

import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.application.storage.port.out.StoragePort;
import org.example.remedy.domain.user.Status;
import org.example.remedy.domain.user.User;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserPersistencePort userPersistencePort;

    @InjectMocks
    private UserServiceImpl userService;

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
        doNothing().when(userPersistencePort).save(any(User.class));

        // when
        userService.withdrawUser(testUser);

        // then
        assertThat(testUser.getStatus()).isEqualTo(Status.WITHDRAWAL);
        assertThat(testUser.getWithdrawalDate()).isNotNull();
    }
}