package org.example.remedy.application.comment.port.in;

public interface CommentService {

    void createComment(String content, Long userId, String droppingId);
}
