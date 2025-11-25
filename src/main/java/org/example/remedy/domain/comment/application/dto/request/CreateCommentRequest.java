package org.example.remedy.domain.comment.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 100, message = "댓글은 100자를 초과할 수 없습니다.")
        String content,

        @NotBlank(message = "드랍핑 ID는 필수입니다.")
        String droppingId
){}
