package org.example.remedy.application.comment.dto.response;

public record CommentResponse(
        Long id,
        String content,
        String droppingId
) {
}
