package org.example.remedy.presentation.title.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 칭호 생성 요청 DTO
 * 
 * 관리자가 새로운 칭호를 생성할 때 사용하는 요청 객체입니다.
 * 
 * @param name 칭호 이름 (필수, 최대 50자)
 * @param description 칭호 설명 (필수)
 * @param price 칭호 가격 (필수, 0 이상)
 */
public record TitleCreateRequest(
        @NotBlank(message = "칭호 이름은 필수입니다.")
        @Size(max = 50, message = "칭호 이름은 최대 50자까지 입력 가능합니다.")
        String name,

        @NotBlank(message = "칭호 설명은 필수입니다.")
        String description,

        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        Integer price
) {}