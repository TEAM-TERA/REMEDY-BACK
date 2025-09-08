package org.example.remedy.application.comment.port.out;

import org.example.remedy.domain.comment.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentPersistencePort {

    void save(Comment comment);

    List<Comment> findAllByDroppingIdDesc(String droppingId);

    Optional<Comment> findById(Long id);

    void delete(Comment comment);
}
