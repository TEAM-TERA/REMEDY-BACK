package org.example.remedy.application.comment.port.in;

import org.example.remedy.application.comment.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    void createComment(String content, Long userId, String droppingId);

    List<CommentResponse> getCommentsByDropping(String droppingId);
}
