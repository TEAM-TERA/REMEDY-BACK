package org.example.remedy.application.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.remedy.presentation.auth.dto.AuthLoginRequest;
import org.example.remedy.presentation.auth.dto.AuthRegisterRequest;
import org.example.remedy.application.auth.exception.InvalidPasswordException;
import org.example.remedy.application.auth.exception.UserAlreadyExistsException;
import org.example.remedy.domain.user.User;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.infrastructure.persistence.user.UserRepository;
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

        User user = User.create(req, password);

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
