package org.example.remedy.domain.dropping.dto.request;

import jakarta.validation.constraints.*;

public record DroppingCreateRequest(
        @NotBlank(message = "노래 ID는 필수입니다")
        @Size(max = 255, message = "노래 ID는 255자를 초과할 수 없습니다")
        String songId,

        @Size(max = 255, message = "내용은 255자를 초과할 수 없습니다")
        String content,

        @NotNull(message = "위도는 필수입니다")
        @DecimalMin(value = "-90.0", message = "위도는 -90도 이상이어야 합니다")
        @DecimalMax(value = "90.0", message = "위도는 90도 이하여야 합니다")
        Double latitude,

        @NotNull(message = "경도는 필수입니다")
        @DecimalMin(value = "-180.0", message = "경도는 -180도 이상이어야 합니다")
        @DecimalMax(value = "180.0", message = "경도는 180도 이하여야 합니다")
        Double longitude,

        @NotBlank(message = "주소는 필수입니다")
        @Size(max = 200, message = "주소는 200자를 초과할 수 없습니다")
        String address
) { }
