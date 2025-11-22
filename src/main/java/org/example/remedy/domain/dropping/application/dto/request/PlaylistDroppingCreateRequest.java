package org.example.remedy.domain.dropping.application.dto.request;

import jakarta.validation.constraints.*;

import java.util.List;

public record PlaylistDroppingCreateRequest(
        @NotBlank(message = "플레이리스트 이름은 필수입니다")
        @Size(max = 100, message = "플레이리스트 이름은 100자를 초과할 수 없습니다")
        String playlistName,

        @NotEmpty(message = "음악 목록은 최소 1개 이상이어야 합니다")
        @Size(min = 1, max = 50, message = "음악 목록은 1~50개여야 합니다")
        List<@NotBlank(message = "음악 ID는 빈 값일 수 없습니다") String> songIds,

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