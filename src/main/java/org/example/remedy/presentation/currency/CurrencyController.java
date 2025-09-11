package org.example.remedy.presentation.currency;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.currency.dto.response.UserCurrencyResponse;
import org.example.remedy.application.currency.port.in.CurrencyService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 통화 API 컨트롤러
 * 
 * 사용자의 통화 정보 조회 기능을 제공하는 REST API 컨트롤러입니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    /**
     * 사용자 통화 정보 조회 API
     * 
     * 인증된 사용자의 통화 보유 현황을 조회합니다.
     * 
     * @param authDetails 인증된 사용자 정보
     * @return 사용자 통화 정보
     */
    @GetMapping
    public ResponseEntity<UserCurrencyResponse> getUserCurrency(
            @AuthenticationPrincipal AuthDetails authDetails) {
        UserCurrencyResponse response = currencyService.getUserCurrency(authDetails.getUser());
        return ResponseEntity.ok(response);
    }
}