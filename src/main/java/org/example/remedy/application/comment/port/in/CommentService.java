package org.example.remedy.application.comment.port.in;

import org.example.remedy.application.comment.dto.response.CommentResponse;
import org.example.remedy.domain.user.User;

import java.util.List;

public interface CommentService {

    void createComment(String content, User user, String droppingId);

    List<CommentResponse> getCommentsByDropping(String droppingId);
}
