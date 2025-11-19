package org.example.remedy.presentation.user;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.dropping.dto.response.DroppingSearchResponse;
import org.example.remedy.application.dropping.port.in.DroppingService;
import org.example.remedy.application.like.port.in.LikeService;
import org.example.remedy.domain.user.User;
import org.example.remedy.presentation.user.dto.request.UserProfileUpdateRequest;
import org.example.remedy.application.user.dto.response.UserProfileImageResponse;
import org.example.remedy.application.user.dto.response.UserProfileResponse;
import org.example.remedy.application.user.port.in.UserService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.global.security.util.CookieManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final DroppingService droppingService;
    private final LikeService likeService;
    private final CookieManager cookieManager;

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

    @GetMapping("/my-drop")
    public ResponseEntity<List<DroppingSearchResponse>> getMyDroppings(@AuthenticationPrincipal AuthDetails authDetails) {
        List<DroppingSearchResponse> responses = droppingService.getUserDroppings(authDetails.getUserId());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my-like")
    public ResponseEntity<List<String>> getLikedDropping(
            @AuthenticationPrincipal AuthDetails authDetails) {

        User user = authDetails.getUser();
        List<String> droppingId = likeService.getLikedDroppingsByUser(user);
        return ResponseEntity.ok(droppingId);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<Void> withdrawal(
            @AuthenticationPrincipal AuthDetails authDetails) {
        userService.withdrawUser(authDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
