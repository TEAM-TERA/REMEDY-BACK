package org.example.remedy.domain.comment.repository;

import org.example.remedy.domain.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {

    void save(Comment comment);

    List<Comment> findAllByDroppingIdDesc(String droppingId);

    Optional<Comment> findById(Long id);

    void delete(Comment comment);

    long countByDroppingId(String droppingId);
}
