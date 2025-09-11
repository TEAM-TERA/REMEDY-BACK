package org.example.remedy.application.title.dto.response;

import java.util.List;

public record UserTitleListResponse(
        List<UserTitleResponse> titles,
        int totalCount,
        UserTitleResponse equippedTitle
) {
    public static UserTitleListResponse from(List<UserTitleResponse> titles) {
        UserTitleResponse equippedTitle = titles.stream()
                .filter(UserTitleResponse::isEquipped)
                .findFirst()
                .orElse(null);
        
        return new UserTitleListResponse(
                titles,
                titles.size(),
                equippedTitle
        );
    }
}