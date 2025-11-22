package org.example.remedy.domain.comment.application.dto.response;

import org.example.remedy.domain.comment.domain.Comment;

public record CommentResponse(
        Long id,
        String content,
        String droppingId,
        String username
) {
    public static CommentResponse of(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getContent(), comment.getDroppingId(), comment.getUser().getUsername());
    }
}
