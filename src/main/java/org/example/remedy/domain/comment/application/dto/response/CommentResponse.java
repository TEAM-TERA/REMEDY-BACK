package org.example.remedy.domain.comment.application.dto.response;

import org.example.remedy.domain.comment.domain.Comment;

public record CommentResponse(
        Long id,
        String content,
        String droppingId,
        String username
) {
}
