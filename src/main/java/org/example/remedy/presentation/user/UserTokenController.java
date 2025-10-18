package org.example.remedy.presentation.user;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.user.port.in.UserTokenService;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserTokenController {

    private final UserTokenService userTokenService;

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> registerToken(@AuthenticationPrincipal AuthDetails authDetails,
                                              @RequestBody String token) {
        userTokenService.saveToken(authDetails.getUserId(), token);
        return ResponseEntity.ok().build();
    }
}
