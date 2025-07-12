package org.example.remedy.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.dto.response.LikeResponse;
import org.example.remedy.domain.like.service.LikeService;
import org.springframework.web.bind.annotation.*;
import org.example.remedy.domain.like.domain.TargetType;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public void like(
            @RequestParam Long userId,
            @RequestParam String targetId,
            @RequestParam TargetType targetType
    ) {
        likeService.like(userId, targetId, targetType);
    }

    @GetMapping("/count")
    public long countLikes(
            @RequestParam String targetId,
            @RequestParam TargetType targetType
    ) {
        return likeService.countLikes(targetId, targetType);
    }
}
