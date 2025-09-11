package org.example.remedy.infrastructure.persistence.title;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.title.port.out.TitlePersistencePort;
import org.example.remedy.domain.title.Title;
import org.example.remedy.domain.title.UserTitle;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaTitleAdapter implements TitlePersistencePort {
    private final TitleRepository titleRepository;
    private final UserTitleRepository userTitleRepository;

    @Override
    public Title save(Title title) {
        return titleRepository.save(title);
    }

    @Override
    public Optional<Title> findById(Long titleId) {
        return titleRepository.findById(titleId);
    }

    @Override
    public List<Title> findAll() {
        return titleRepository.findAll();
    }

    @Override
    public List<Title> findByIsActiveTrue() {
        return titleRepository.findByIsActiveTrue();
    }

    @Override
    public boolean existsByName(String name) {
        return titleRepository.existsByName(name);
    }

    @Override
    public UserTitle save(UserTitle userTitle) {
        return userTitleRepository.save(userTitle);
    }

    @Override
    public Optional<UserTitle> findByUserIdAndTitleId(Long userId, Long titleId) {
        return userTitleRepository.findByUserIdAndTitleId(userId, titleId);
    }

    @Override
    public List<UserTitle> findByUserId(Long userId) {
        return userTitleRepository.findByUserId(userId);
    }

    @Override
    public Optional<UserTitle> findByUserIdAndIsEquippedTrue(Long userId) {
        return userTitleRepository.findByUserIdAndIsEquippedTrue(userId);
    }

    @Override
    public void unequipAllTitles(Long userId) {
        userTitleRepository.unequipAllTitles(userId);
    }
}