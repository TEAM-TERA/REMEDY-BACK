package org.example.remedy.domain.auth.domain;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.auth.dto.request.AuthLoginRequestDto;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.domain.auth.dto.request.AuthRegisterRequestDto;
import org.example.remedy.domain.user.exception.UserAlreadyExistsException;
import org.example.remedy.domain.user.exception.UserNotFoundException;
import org.example.remedy.domain.user.repository.UserRepository;
import org.example.remedy.domain.user.type.Provider;
import org.example.remedy.domain.user.type.Role;
import org.example.remedy.global.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public void createUser(AuthRegisterRequestDto req) {
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

    public void login(AuthLoginRequestDto req, HttpServletResponse res) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(UserNotFoundException::new);

        if(!passwordEncoder.matches(req.password(), user.getPassword())) throw new UserNotFoundException();

        jwtTokenProvider.createTokens(user.getEmail(), res);
    }
}
