package org.example.remedy.application.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.auth.port.in.AuthService;
import org.example.remedy.presentation.auth.dto.AuthLoginRequest;
import org.example.remedy.presentation.auth.dto.AuthRegisterRequest;
import org.example.remedy.application.auth.exception.InvalidPasswordException;
import org.example.remedy.application.auth.exception.UserAlreadyExistsException;
import org.example.remedy.domain.user.User;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.example.remedy.global.security.util.CookieManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CookieManager cookieManager;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_REFRESH_KEY_PREFIX = "refreshToken:";

    @Transactional
    public void signup (AuthRegisterRequest req) {
        if (userPersistencePort.existsUserByEmail(req.email())) throw UserAlreadyExistsException.EXCEPTION;

        String password = passwordEncoder.encode(req.password());

        User user = User.create(req, password);

        userPersistencePort.save(user);
    }
  
    public void login(AuthLoginRequest req, HttpServletResponse res) {
        String email = req.email();

        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(()-> UserNotFoundException.EXCEPTION);

        if(!passwordEncoder.matches(req.password(), user.getPassword())) throw InvalidPasswordException.EXCEPTION;

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
