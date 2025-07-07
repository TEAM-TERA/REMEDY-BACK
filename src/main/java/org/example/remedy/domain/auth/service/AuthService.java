package org.example.remedy.domain.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.auth.dto.AuthLoginRequest;
import org.example.remedy.domain.auth.exception.InvalidPasswordException;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.auth.dto.AuthRegisterRequest;
import org.example.remedy.domain.auth.exception.UserAlreadyExistsException;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.user.type.Provider;
import org.example.remedy.domain.user.type.Role;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.example.remedy.global.security.util.CookieManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CookieManager cookieManager;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_REFRESH_KEY_PREFIX = "refreshToken:";

    @Transactional
    public void createUser(AuthRegisterRequest req) {
        if (userRepository.existsUserByEmail(req.email())) throw new UserAlreadyExistsException();

        String password = passwordEncoder.encode(req.password());

        User user = User.builder()
                .username(req.username())
                .password(password)
                .profileImage("https://mblogthumb-phinf.pstatic.net/MjAyMDExMDFfODMg/MDAxNjA0MjI4ODc1MDgz.gQ3xcHrLXaZyxcFAoEcdB7tJWuRs7fKgOxQwPvsTsrUg.0OBtKHq2r3smX5guFQtnT7EDwjzksz5Js0wCV4zjfpcg.JPEG.gambasg/%EC%9C%A0%ED%8A%9C%EB%B8%8C_%EA%B8%B0%EB%B3%B8%ED%94%84%EB%A1%9C%ED%95%84_%EB%B3%B4%EB%9D%BC.jpg?type=w400")
                .email(req.email())
                .role(Role.ROLE_USER)
                .provider(Provider.SELF)
                .birthdate(req.birthdate())
                .gender(req.gender())
                .build();

        userRepository.save(user);
    }
  
    public void login(AuthLoginRequest req, HttpServletResponse res) {
        String email = req.email();

        User user = userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        if(!passwordEncoder.matches(req.password(), user.getPassword())) throw new InvalidPasswordException();

        String accessToken = tokenProvider.createAccessToken(email);
        String refreshToken = tokenProvider.createRefreshToken(email);

        redisTemplate.opsForValue().set(REDIS_REFRESH_KEY_PREFIX+email, refreshToken);

        cookieManager.setAuthorizationHeader(res, accessToken);
        cookieManager.setRefreshTokenCookie(res, refreshToken);
    }

    public void refresh(Cookie cookie, HttpServletResponse res) {
        cookieManager.setAuthorizationHeader(res, tokenProvider.refresh(cookie));
    }
}
