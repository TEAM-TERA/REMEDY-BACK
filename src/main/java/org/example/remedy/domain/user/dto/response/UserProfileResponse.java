package org.example.remedy.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private final String username;
    private final String ProfileImageUrl;
}
