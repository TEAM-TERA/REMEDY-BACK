package org.example.remedy.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.service.UserService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal AuthDetails authDetails) {
        return ResponseEntity.ok(
                userService.getMyProfile(authDetails.getUserId())
        );
    }

    @PatchMapping
    public ResponseEntity<Void> updateProfile(
            @RequestBody @Valid UserProfileUpdateRequest req,
            @AuthenticationPrincipal AuthDetails authDetails) {

        userService.updateUserProfile(req, authDetails.getUsername());
        return ResponseEntity.ok().build();
    }

}
