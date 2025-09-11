package org.example.remedy.presentation.title;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.title.dto.response.TitleListResponse;
import org.example.remedy.application.title.dto.response.TitleResponse;
import org.example.remedy.application.title.dto.response.UserTitleListResponse;
import org.example.remedy.application.title.dto.response.UserTitleResponse;
import org.example.remedy.application.title.port.in.TitleService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.example.remedy.presentation.title.dto.request.TitleCreateRequest;
import org.example.remedy.presentation.title.dto.request.TitleUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/titles")
public class TitleController {
    private final TitleService titleService;

    @PostMapping
    public ResponseEntity<TitleResponse> createTitle(
            @RequestBody @Valid TitleCreateRequest request,
            @AuthenticationPrincipal AuthDetails authDetails) {
        TitleResponse response = titleService.createTitle(
                request.name(),
                request.description(),
                request.price(),
                authDetails.getUser()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<TitleListResponse> getAllTitles() {
        TitleListResponse response = titleService.getAllTitles();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<TitleListResponse> getActiveTitles() {
        TitleListResponse response = titleService.getActiveTitles();
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{titleId}")
    public ResponseEntity<TitleResponse> updateTitle(
            @PathVariable Long titleId,
            @RequestBody @Valid TitleUpdateRequest request,
            @AuthenticationPrincipal AuthDetails authDetails) {
        TitleResponse response = titleService.updateTitle(
                titleId,
                request.name(),
                request.description(),
                request.price(),
                authDetails.getUser()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{titleId}/deactivate")
    public ResponseEntity<Void> deactivateTitle(
            @PathVariable Long titleId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        titleService.deactivateTitle(titleId, authDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{titleId}/activate")
    public ResponseEntity<Void> activateTitle(
            @PathVariable Long titleId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        titleService.activateTitle(titleId, authDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<UserTitleListResponse> getUserTitles(
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserTitleListResponse response = titleService.getUserTitles(authDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{titleId}/purchase")
    public ResponseEntity<UserTitleResponse> purchaseTitle(
            @PathVariable Long titleId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserTitleResponse response = titleService.purchaseTitle(titleId, authDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{titleId}/equip")
    public ResponseEntity<UserTitleResponse> equipTitle(
            @PathVariable Long titleId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserTitleResponse response = titleService.equipTitle(titleId, authDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{titleId}/unequip")
    public ResponseEntity<Void> unequipTitle(
            @PathVariable Long titleId,
            @AuthenticationPrincipal AuthDetails authDetails) {
        titleService.unequipTitle(titleId, authDetails.getUser());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/equipped")
    public ResponseEntity<UserTitleResponse> getCurrentEquippedTitle(
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserTitleResponse response = titleService.getCurrentEquippedTitle(authDetails.getUser());
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }
}