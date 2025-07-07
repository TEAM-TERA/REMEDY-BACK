package org.example.remedy.domain.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.auth.service.AuthService;
import org.example.remedy.domain.auth.dto.AuthLoginRequest;
import org.example.remedy.domain.auth.dto.AuthRegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AuthRegisterRequest req) {
        authService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody AuthLoginRequest req, HttpServletResponse res) {
        authService.login(req, res);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(@CookieValue(value = "refresh_token") Cookie cookie, HttpServletResponse res) {
        authService.refresh(cookie, res);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
