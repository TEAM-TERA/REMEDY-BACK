package org.example.remedy.domain.auth.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.remedy.domain.auth.dto.AuthLoginRequest;
import org.example.remedy.domain.auth.dto.AuthRegisterRequest;
import org.example.remedy.domain.auth.exception.InvalidPasswordException;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.auth.exception.UserAlreadyExistsException;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.example.remedy.global.security.util.CookieManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider jwtTokenProvider;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private AuthService authService;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private CookieManager cookieManager;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {

        //given
        AuthRegisterRequest dto = new AuthRegisterRequest("sejin", "password123", "test@gmail.com", LocalDate.now(), true);
        given(userRepository.existsUserByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPw");

        //when
        authService.createUser(dto);
        //then
        then(userRepository).should(times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("중복 이메일이면 회원가입 실패")
    void register_duplicateEmail() {

        //given
        AuthRegisterRequest dto = new AuthRegisterRequest("sejin", "password123", "test@gmail.com", LocalDate.now(), true);
        given(userRepository.existsUserByEmail(anyString())).willReturn(true);

        //when & then
        assertThrows(UserAlreadyExistsException.class, () -> authService.createUser(dto));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {

        //given
        User user = User.builder()
                .email("test@gmail.com")
                .password("password123")
                .build();

        AuthLoginRequest dto = new AuthLoginRequest("password123", "test@gmail.com");

        given(userRepository.findByEmail(dto.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(dto.password(), user.getPassword())).willReturn(true);
        given(jwtTokenProvider.createAccessToken(user.getEmail())).willReturn("mockAccessToken");
        given(jwtTokenProvider.createRefreshToken(user.getEmail())).willReturn("mockRefreshToken");
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        doNothing().when(cookieManager).setAuthorizationHeader(any(HttpServletResponse.class), anyString());


        //when
        authService.login(dto, response);

        //then
        then(jwtTokenProvider).should(times(1)).createAccessToken(user.getEmail());
        then(jwtTokenProvider).should(times(1)).createRefreshToken(user.getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 시 예외발생")
    void login_EmailNotFound() {

        //given
        AuthLoginRequest dto = new AuthLoginRequest("password123", "test@gmail.com");
        given(userRepository.findByEmail(dto.email())).willReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> authService.login(dto, response));
    }

    @Test
    @DisplayName("비밀번호가 틀리면 로그인 실패")
    void login_PasswordMismatch() {

        // given
        User user = User.builder()
                .email("test@gmail.com")
                .password("password123")
                .build();

        AuthLoginRequest dto = new AuthLoginRequest("password123", "test@gmail.com");

        given(userRepository.findByEmail(dto.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(dto.password(), user.getPassword())).willReturn(false);

        // when & then
        assertThrows(InvalidPasswordException.class, () -> authService.login(dto, response));
    }
}
