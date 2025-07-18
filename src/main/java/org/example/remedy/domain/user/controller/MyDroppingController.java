package org.example.remedy.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.example.remedy.domain.user.service.UserQueryService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dropping")
public class MyDroppingController {

    private final UserQueryService userQueryService;

    @GetMapping("/my-drop")
    public ResponseEntity<List<MyDroppingResponse>> getMyDroppings(
            @AuthenticationPrincipal AuthDetails authDetails) {
        List<MyDroppingResponse> responses = userQueryService.getUserDroppings(authDetails.getUserId());
        return ResponseEntity.ok(responses);
    }
}
