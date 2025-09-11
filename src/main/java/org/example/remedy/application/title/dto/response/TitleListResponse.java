package org.example.remedy.application.title.dto.response;

import org.example.remedy.domain.title.Title;

import java.util.List;

public record TitleListResponse(
        List<TitleResponse> titles,
        int totalCount
) {
    public static TitleListResponse from(List<Title> titles) {
        List<TitleResponse> titleResponses = titles.stream()
                .map(TitleResponse::from)
                .toList();
        
        return new TitleListResponse(titleResponses, titles.size());
    }
}