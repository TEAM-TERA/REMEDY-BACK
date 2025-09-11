package org.example.remedy.application.title.port.out;

import org.example.remedy.domain.title.Title;
import org.example.remedy.domain.title.UserTitle;

import java.util.List;
import java.util.Optional;

public interface TitlePersistencePort {
    Title save(Title title);
    
    Optional<Title> findById(Long titleId);
    
    List<Title> findAll();
    
    List<Title> findByIsActiveTrue();
    
    boolean existsByName(String name);
    
    UserTitle save(UserTitle userTitle);
    
    Optional<UserTitle> findByUserIdAndTitleId(Long userId, Long titleId);
    
    List<UserTitle> findByUserId(Long userId);
    
    Optional<UserTitle> findByUserIdAndIsEquippedTrue(Long userId);
    
    void unequipAllTitles(Long userId);
}