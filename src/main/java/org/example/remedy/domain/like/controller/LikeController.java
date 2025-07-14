package org.example.remedy.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.dto.response.LikeResponse;
import org.example.remedy.domain.like.service.LikeService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.web.bind.annotation.*;
import org.example.remedy.domain.like.domain.TargetType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public void like(
            @AuthenticationPrincipal AuthDetails authDetails,
            @RequestParam String targetId,
            @RequestParam TargetType targetType
    ) {
        likeService.like(authDetails.getUserId(), targetId, targetType);
    }

    @GetMapping("/count")
    public long countLikes(
            @RequestParam String targetId,
            @RequestParam TargetType targetType
    ) {
        return likeService.countLikes(targetId, targetType);
    }
}
