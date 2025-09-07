package org.example.remedy.presentation.like;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.like.port.in.LikeService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.application.like.dto.response.LikeCountResponse;
import org.example.remedy.presentation.like.dto.request.LikeRequest;
import org.example.remedy.application.like.LikeServiceImpl;
import org.example.remedy.application.like.dto.response.LikeToggleResponse;
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
        boolean liked = likeService.toggleLike(authDetails.getUserId(), request.droppingId());
        return ResponseEntity.ok(new LikeToggleResponse(liked));
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<LikeCountResponse> getLikeCountByUser(@PathVariable Long userId) {
        long count = likeService.getLikeCountByUser(userId);
        return ResponseEntity.ok(new LikeCountResponse(count));
    }

    @GetMapping("/count/dropping/{droppingId}")
    public ResponseEntity<LikeCountResponse> getLikeCountByDropping(@PathVariable String droppingId) {
        long count = likeService.getLikeCountByDropping(droppingId);
        return ResponseEntity.ok(new LikeCountResponse(count));
    }
}

