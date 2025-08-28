package org.example.remedy.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.dto.request.LikeRequest;
import org.example.remedy.domain.like.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Map<String, Boolean>> toggleLike(@RequestBody LikeRequest request) {
        boolean liked = likeService.toggleLike(request.userId(), request.droppingId());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Map<String, Long>> getLikeCountByUser(@PathVariable Long userId) {
        long count = likeService.getLikeCountByUser(userId);
        return ResponseEntity.ok(Map.of("likeCount", count));
    }
}

