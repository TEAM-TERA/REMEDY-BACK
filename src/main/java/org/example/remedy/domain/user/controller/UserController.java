package org.example.remedy.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.service.UserService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal AuthDetails authDetails) {
        return ResponseEntity.ok(
                userService.getMyProfile(authDetails.getUserId())
        );
    }
}
