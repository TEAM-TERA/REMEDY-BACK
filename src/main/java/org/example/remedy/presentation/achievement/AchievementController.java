package org.example.remedy.presentation.achievement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.achievement.dto.response.AchievementListResponse;
import org.example.remedy.application.achievement.dto.response.AchievementResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementListResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementResponse;
import org.example.remedy.application.achievement.port.in.AchievementService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.achievement.dto.request.AchievementCreateRequest;
import org.example.remedy.presentation.achievement.dto.request.AchievementUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/achievements")
public class AchievementController {
    private final AchievementService achievementService;

    /**
     * 도전과제 생성 (관리자 전용)
     * SecurityConfig에서 /admin/** 경로로 관리자 권한 검증 처리
     */
    @PostMapping
    public ResponseEntity<AchievementResponse> createAchievement(
            @RequestBody @Valid AchievementCreateRequest request,
            @AuthenticationPrincipal AuthDetails authDetails) {
        AchievementResponse response = achievementService.createAchievement(
                request.title(),
                request.type(),
                request.period(), // 일일/상시 기간 정보 전달
                request.targetValue(),
                request.rewardAmount(),
                authDetails.getUser()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<AchievementListResponse> getAllAchievements() {
        AchievementListResponse response = achievementService.getAllAchievements();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<AchievementListResponse> getActiveAchievements() {
        AchievementListResponse response = achievementService.getActiveAchievements();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{achievementId}")
    public ResponseEntity<AchievementResponse> updateAchievement(
            @PathVariable Long achievementId,
            @RequestBody @Valid AchievementUpdateRequest request,
            @AuthenticationPrincipal AuthDetails authDetails) {
        AchievementResponse response = achievementService.updateAchievement(
                achievementId,
                request.title(),
                request.targetValue(),
                request.rewardAmount(),
                authDetails.getUser()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{achievementId}/deactivate")
    public ResponseEntity<Void> deactivateAchievement(
            @PathVariable Long achievementId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        achievementService.deactivateAchievement(achievementId, authDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{achievementId}/activate")
    public ResponseEntity<Void> activateAchievement(
            @PathVariable Long achievementId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        achievementService.activateAchievement(achievementId, authDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<UserAchievementListResponse> getUserAchievements(
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserAchievementListResponse response = achievementService.getUserAchievements(authDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{achievementId}/claim")
    public ResponseEntity<UserAchievementResponse> claimReward(
            @PathVariable Long achievementId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserAchievementResponse response = achievementService.claimReward(achievementId, authDetails.getUser());
        return ResponseEntity.ok(response);
    }
}