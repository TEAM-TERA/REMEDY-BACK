package org.example.remedy.domain.like.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.application.service.LikeService;
import org.example.remedy.domain.user.domain.User;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.domain.like.application.dto.response.LikeCountResponse;
import org.example.remedy.domain.like.application.dto.request.LikeRequest;
import org.example.remedy.domain.like.application.dto.response.LikeToggleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeToggleResponse> toggleLike(
            @AuthenticationPrincipal AuthDetails authDetails,
            @RequestBody @Valid LikeRequest request) {

        User user = authDetails.getUser();
        boolean liked = likeService.toggleLike(user, request.droppingId());
        return ResponseEntity.ok(new LikeToggleResponse(liked));
    }

    @GetMapping("/count/user")
    public ResponseEntity<LikeCountResponse> getLikeCountByUser(
            @AuthenticationPrincipal AuthDetails authDetails) {

        User user = authDetails.getUser();
        long count = likeService.getLikeCountByUser(user);
        return ResponseEntity.ok(new LikeCountResponse(count));
    }

    @GetMapping("/count/dropping/{droppingId}")
    public ResponseEntity<LikeCountResponse> getLikeCountByDropping(@PathVariable String droppingId) {

        long count = likeService.getLikeCountByDropping(droppingId);
        return ResponseEntity.ok(new LikeCountResponse(count));
    }

}


