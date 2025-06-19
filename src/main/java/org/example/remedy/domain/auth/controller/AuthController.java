package org.example.remedy.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.auth.domain.AuthService;
import org.example.remedy.domain.auth.dto.request.AuthRegisterRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody AuthRegisterRequestDto req) {
        authService.createUser(req);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
