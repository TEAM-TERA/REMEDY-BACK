package org.example.remedy.domain.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.dto.response.UserProfileImageResponse;
import org.example.remedy.domain.user.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.service.UserService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal AuthDetails authDetails) {
        return ResponseEntity.ok(
                userService.getMyProfile(authDetails.getUser())
        );
    }

    @PatchMapping
    public ResponseEntity<Void> updateProfile(
            @RequestBody @Valid UserProfileUpdateRequest req,
            @AuthenticationPrincipal AuthDetails authDetails) {

        userService.updateUserProfile(req, authDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @PutMapping(path = "/profile-image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserProfileImageResponse> updateProfileImage(@RequestParam MultipartFile image, @AuthenticationPrincipal AuthDetails authDetails) {
        UserProfileImageResponse res = userService.updateUserProfileImage(image, authDetails.getUser());
        return ResponseEntity.ok(res);
    }
}
