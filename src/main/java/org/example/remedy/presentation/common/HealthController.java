package org.example.remedy.presentation.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @ResponseStatus(HttpStatus.OK)
    public void healthCheck() {
        // 로드밸런서 헬스 체크용
    }
}
