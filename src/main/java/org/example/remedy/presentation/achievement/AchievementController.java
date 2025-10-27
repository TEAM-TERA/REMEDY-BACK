package org.example.remedy.presentation.achievement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.achievement.dto.response.AchievementListResponse;
import org.example.remedy.application.achievement.dto.response.AchievementResponse;
import org.example.remedy.application.achievement.dto.response.PagedUserAchievementListResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementListResponse;
import org.example.remedy.application.achievement.dto.response.UserAchievementResponse;
import org.example.remedy.application.achievement.port.in.AchievementService;
import org.example.remedy.domain.achievement.AchievementPeriod;
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

    /**
     * 현재 활성화된 도전과제 조회 (사용자의 진행 상황 포함)
     * 유저 인증 필요
     *
     * @deprecated 페이징 처리된 API를 사용하세요
     */
    @Deprecated
    @GetMapping("/all")
    public ResponseEntity<UserAchievementListResponse> getActiveAchievementsWithProgress(
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserAchievementListResponse response = achievementService.getActiveAchievementsWithUserProgress(authDetails.getUser());
        return ResponseEntity.ok(response);
    }

    /**
     * 활성화된 도전과제 조회 (기간별 필터링 및 페이징 처리)
     * 쿼리 파라미터로 일일/상시 구분 및 페이징 정보를 받습니다.
     *
     * @param period 도전과제 기간 (DAILY, PERMANENT, null이면 전체 조회)
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @param authDetails 인증된 사용자 정보
     * @return 페이징된 도전과제 목록 및 사용자 진행 상황
     */
    @GetMapping
    public ResponseEntity<PagedUserAchievementListResponse> getActiveAchievementsWithProgressPaged(
            @RequestParam(required = false) AchievementPeriod period,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal AuthDetails authDetails) {
        PagedUserAchievementListResponse response = achievementService.getActiveAchievementsWithUserProgressPaged(
                authDetails.getUser(), period, page, size);
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


    @PostMapping("/{achievementId}/claim")
    public ResponseEntity<UserAchievementResponse> claimReward(
            @PathVariable Long achievementId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserAchievementResponse response = achievementService.claimReward(achievementId, authDetails.getUser());
        return ResponseEntity.ok(response);
    }
}