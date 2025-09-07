package org.example.remedy.presentation.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank
        @Size(max = 100)
        String content,

        @NotBlank
        String droppingId
){}
