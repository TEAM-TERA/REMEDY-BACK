package org.example.remedy.presentation.title.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record TitleUpdateRequest(
        @Size(max = 50, message = "칭호 이름은 최대 50자까지 입력 가능합니다.")
        String name,

        String description,

        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Integer price
) {}