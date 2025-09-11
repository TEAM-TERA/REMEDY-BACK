package org.example.remedy.application.comment.dto.response;

import org.example.remedy.domain.comment.Comment;

public record CommentResponse(
        Long id,
        String content,
        String droppingId
) {
    public static CommentResponse of(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContent(), comment.getDroppingId());
    }
}
