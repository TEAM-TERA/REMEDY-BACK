package org.example.remedy.application.comment.port.out;

import org.example.remedy.domain.comment.Comment;

import java.util.List;

public interface CommentPersistencePort {
    void save(Comment comment);

    List<Comment> findAllByDroppingId(String droppingId);
}
