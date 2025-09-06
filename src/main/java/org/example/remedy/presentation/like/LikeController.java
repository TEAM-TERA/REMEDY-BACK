package org.example.remedy.presentation.like;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.presentation.like.dto.LikeRequest;
import org.example.remedy.application.like.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @AuthenticationPrincipal(expression = "userId") Long userId,
            @RequestBody @Valid LikeRequest request) {
        boolean liked = likeService.toggleLike(userId, request.droppingId());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Map<String, Long>> getLikeCountByUser(@PathVariable Long userId) {
        long count = likeService.getLikeCountByUser(userId);
        return ResponseEntity.ok(Map.of("likeCount", count));
    }

    @GetMapping("/count/dropping/{droppingId}")
    public ResponseEntity<Map<String, Long>> getLikeCountByDropping(@PathVariable String droppingId) {
        long count = likeService.getLikeCountByDropping(droppingId);
        return ResponseEntity.ok(Map.of("likeCount", count));
    }
}

