package org.example.remedy.infrastructure.persistence.title;

import org.example.remedy.domain.title.Title;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TitleRepository extends JpaRepository<Title, Long> {
    List<Title> findByIsActiveTrue();
    boolean existsByName(String name);
}