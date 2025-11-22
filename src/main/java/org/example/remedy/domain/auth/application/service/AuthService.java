package org.example.remedy.domain.auth.application.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.auth.application.exception.InvalidPasswordException;
import org.example.remedy.domain.auth.application.exception.UserAlreadyExistsException;
import org.example.remedy.domain.user.application.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.user.domain.Status;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.global.security.jwt.TokenProvider;
import org.example.remedy.global.security.util.CookieManager;
import org.example.remedy.domain.auth.application.dto.request.AuthLoginRequest;
import org.example.remedy.domain.auth.application.dto.request.AuthRegisterRequest;
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

    @Transactional
    public void signup (AuthRegisterRequest req) {
        if (userRepository.existsUserByEmail(req.email())) throw UserAlreadyExistsException.EXCEPTION;

        String password = passwordEncoder.encode(req.password());

        User user = User.create(req, password);

        userRepository.save(user);
    }

    public void login(AuthLoginRequest req, HttpServletResponse res) {
        String email = req.email();

        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> UserNotFoundException.EXCEPTION);

        if(!passwordEncoder.matches(req.password(), user.getPassword())) throw InvalidPasswordException.EXCEPTION;

        if(user.getStatus() == Status.WITHDRAWAL){
            user.reactivate();
            userRepository.save(user);
        }

        String accessToken = tokenProvider.createAccessToken(email);

        cookieManager.setAuthorizationHeader(res, accessToken);
    }
}
