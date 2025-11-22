package org.example.remedy.domain.comment.application.mapper;

import org.example.remedy.domain.comment.application.dto.response.CommentResponse;
import org.example.remedy.domain.comment.domain.Comment;
import org.example.remedy.domain.user.domain.User;

public class CommentMapper {
  public static CommentResponse toResponse(Comment comment) {
    return new CommentResponse(
        comment.getId(),
        comment.getContent(),
        comment.getDroppingId(),
        comment.getUser().getUsername());
  }

  public static Comment toEntity(String content, User user, String droppingId) {
    return new Comment(content, user, droppingId);
  }
}
