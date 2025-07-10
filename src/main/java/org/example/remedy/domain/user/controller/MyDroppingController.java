package org.example.remedy.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.example.remedy.domain.user.service.MyDroppingService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dropping")
public class MyDroppingController {

    private final MyDroppingService myDroppingService;

    @GetMapping("/my-drop")
    public ResponseEntity<List<MyDroppingResponse>> getMyDroppings(
            @AuthenticationPrincipal AuthDetails authDetails) {
        try {
            List<MyDroppingResponse> responses =
                    myDroppingService.getMyDroppings(authDetails.getUserId()).get();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
