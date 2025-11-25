package org.example.remedy.domain.dropping.application.dto.request;

import jakarta.validation.constraints.*;
import org.example.remedy.domain.dropping.application.validation.ValidDroppingType;
import org.example.remedy.domain.dropping.domain.DroppingType;

import java.util.List;

@ValidDroppingType
public record DroppingCreateRequest(
        @NotNull(message = "드랍 타입은 필수입니다")
        DroppingType type,

		//MUSIC
        String songId,

		//VOTE
        String topic,
        List<String> options,

		//PLAYLIST
        String playlistId,

        String playlistName,
        List<String> songIds,

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
