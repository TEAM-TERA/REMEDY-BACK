package org.example.remedy.domain.user.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.dropping.application.dto.response.DroppingSearchListResponse;
import org.example.remedy.domain.dropping.application.service.DroppingServiceFacade;
import org.example.remedy.domain.like.application.dto.response.LikeDroppingResponse;
import org.example.remedy.domain.like.application.service.LikeService;
import org.example.remedy.domain.user.application.dto.request.UserProfileUpdateRequest;
import org.example.remedy.domain.user.application.dto.response.UserProfileImageResponse;
import org.example.remedy.domain.user.application.dto.response.UserProfileResponse;
import org.example.remedy.domain.user.application.service.UserService;
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
    private final DroppingServiceFacade droppingService;
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
    public ResponseEntity<DroppingSearchListResponse> getMyDroppings(@AuthenticationPrincipal AuthDetails authDetails) {
        DroppingSearchListResponse response = droppingService.getUserDroppings(authDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-like")
    public ResponseEntity<List<LikeDroppingResponse>> getLikedDropping(
            @AuthenticationPrincipal AuthDetails authDetails) {
        List<LikeDroppingResponse> likedDroppings = likeService.getLikeDroppingsDetailByUser(authDetails.getUser());
        return ResponseEntity.ok(likedDroppings);
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<Void> withdrawal(
            @AuthenticationPrincipal AuthDetails authDetails) {
        userService.withdrawUser(authDetails.getUser());
        return ResponseEntity.ok().build();
    }
}
