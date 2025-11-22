package org.example.remedy.domain.comment.repository;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.comment.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final CommentPersistenceRepository commentRepository;

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findAllByDroppingIdDesc(String droppingId) {
        return commentRepository.findByDroppingIdOrderByIdDesc(droppingId);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public long countByDroppingId(String droppingId) {
        return commentRepository.countByDroppingId(droppingId);
    }
}
