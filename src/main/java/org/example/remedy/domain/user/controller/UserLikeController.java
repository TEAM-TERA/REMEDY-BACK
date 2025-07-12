package org.example.remedy.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.like.dto.response.LikeResponse;
import org.example.remedy.domain.user.facade.UserLikeFacade;
import org.example.remedy.global.security.auth.AuthDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/like")
public class UserLikeController {

    private final UserLikeFacade userLikeFacade;

    @GetMapping("/my-like")
    public List<LikeResponse> getMyLikes(@AuthenticationPrincipal AuthDetails authDetails) {
        return userLikeFacade.getMyLikes(authDetails.getUserId());
    }
}