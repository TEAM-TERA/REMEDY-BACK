package org.example.remedy.domain.comment.repository;

import org.example.remedy.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentPersistenceRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByDroppingIdOrderByIdDesc(String droppingId);

    long countByDroppingId(String droppingId);
}
