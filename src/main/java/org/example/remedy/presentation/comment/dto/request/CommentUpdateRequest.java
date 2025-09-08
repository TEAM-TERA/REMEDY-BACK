package org.example.remedy.presentation.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
        @NotBlank(message = "내용은 필수입니다.")
        @Size(max = 100, message = "댓글은 100자를 초과할 수 없습니다.")
        String content
        ) {}
