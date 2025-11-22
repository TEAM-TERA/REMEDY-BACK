package org.example.remedy.domain.comment.application.dto.response;

public record CommentResponse(
        Long id,
        String content,
        String droppingId,
        String username
) {
}
