package org.example.remedy.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.domain.user.dto.response.MyDroppingResponse;
import org.example.remedy.domain.user.service.MyDroppingService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
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
                    myDroppingService.getMyDroppings(authDetails.getUserId())
                            .get(2, TimeUnit.SECONDS);
            return ResponseEntity.ok(responses);
        } catch (TimeoutException e) {

            log.warn("드롭 목록 조회 타임아웃: userId={}", authDetails.getUserId());
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build();
        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
            log.error("드롭 목록 조회 중단: userId={}", authDetails.getUserId(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } catch (Exception e) {

            log.error("드롭 목록 조회 실패: userId={}", authDetails.getUserId(), e);
            return ResponseEntity.internalServerError().build();
        }

    }
}