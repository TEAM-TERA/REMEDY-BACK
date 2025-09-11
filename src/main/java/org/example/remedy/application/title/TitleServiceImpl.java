package org.example.remedy.application.title;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.currency.port.in.CurrencyService;
import org.example.remedy.application.title.dto.response.TitleListResponse;
import org.example.remedy.application.title.dto.response.TitleResponse;
import org.example.remedy.application.title.dto.response.UserTitleListResponse;
import org.example.remedy.application.title.dto.response.UserTitleResponse;
import org.example.remedy.application.title.exception.TitleAlreadyExistsException;
import org.example.remedy.application.title.exception.TitleAlreadyOwnedException;
import org.example.remedy.application.title.exception.TitleNotFoundException;
import org.example.remedy.application.title.exception.TitleNotOwnedException;
import org.example.remedy.application.title.port.in.TitleService;
import org.example.remedy.application.title.port.out.TitlePersistencePort;
import org.example.remedy.domain.title.Title;
import org.example.remedy.domain.title.UserTitle;
import org.example.remedy.domain.user.Role;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TitleServiceImpl implements TitleService {
    private final TitlePersistencePort titlePersistencePort;
    private final CurrencyService currencyService;

    @Override
    @Transactional
    public TitleResponse createTitle(String name, String description, Integer price, User admin) {
        validateAdminRole(admin);
        
        if (titlePersistencePort.existsByName(name)) {
            throw TitleAlreadyExistsException.INSTANCE;
        }
        
        Title title = Title.create(name, description, price, admin.getUserId());
        Title savedTitle = titlePersistencePort.save(title);
        
        return TitleResponse.from(savedTitle);
    }

    @Override
    @Transactional(readOnly = true)
    public TitleListResponse getAllTitles() {
        List<Title> titles = titlePersistencePort.findAll();
        return TitleListResponse.from(titles);
    }

    @Override
    @Transactional(readOnly = true)
    public TitleListResponse getActiveTitles() {
        List<Title> titles = titlePersistencePort.findByIsActiveTrue();
        return TitleListResponse.from(titles);
    }

    @Override
    @Transactional
    public TitleResponse updateTitle(Long titleId, String name, String description, Integer price, User admin) {
        validateAdminRole(admin);
        
        Title title = titlePersistencePort.findById(titleId)
                .orElseThrow(() -> TitleNotFoundException.INSTANCE);
        
        if (name != null && !name.equals(title.getName()) && titlePersistencePort.existsByName(name)) {
            throw TitleAlreadyExistsException.INSTANCE;
        }
        
        title.updateInfo(name, description, price);
        Title savedTitle = titlePersistencePort.save(title);
        
        return TitleResponse.from(savedTitle);
    }

    @Override
    @Transactional
    public void deactivateTitle(Long titleId, User admin) {
        validateAdminRole(admin);
        
        Title title = titlePersistencePort.findById(titleId)
                .orElseThrow(() -> TitleNotFoundException.INSTANCE);
        
        title.deactivate();
        titlePersistencePort.save(title);
    }

    @Override
    @Transactional
    public void activateTitle(Long titleId, User admin) {
        validateAdminRole(admin);
        
        Title title = titlePersistencePort.findById(titleId)
                .orElseThrow(() -> TitleNotFoundException.INSTANCE);
        
        title.activate();
        titlePersistencePort.save(title);
    }

    @Override
    @Transactional(readOnly = true)
    public UserTitleListResponse getUserTitles(User user) {
        List<UserTitle> userTitles = titlePersistencePort.findByUserId(user.getUserId());
        
        List<UserTitleResponse> responses = userTitles.stream()
                .map(ut -> {
                    Title title = titlePersistencePort.findById(ut.getTitleId())
                            .orElseThrow(() -> TitleNotFoundException.INSTANCE);
                    return UserTitleResponse.from(ut, title);
                })
                .toList();
        
        return UserTitleListResponse.from(responses);
    }

    @Override
    @Transactional
    public UserTitleResponse purchaseTitle(Long titleId, User user) {
        Title title = titlePersistencePort.findById(titleId)
                .orElseThrow(() -> TitleNotFoundException.INSTANCE);
        
        if (!title.isActive()) {
            throw TitleNotFoundException.INSTANCE;
        }
        
        Optional<UserTitle> existingUserTitle = titlePersistencePort.findByUserIdAndTitleId(user.getUserId(), titleId);
        if (existingUserTitle.isPresent()) {
            throw TitleAlreadyOwnedException.INSTANCE;
        }
        
        currencyService.spendCurrency(user, title.getPrice());
        
        UserTitle userTitle = UserTitle.create(user.getUserId(), titleId);
        UserTitle savedUserTitle = titlePersistencePort.save(userTitle);
        
        return UserTitleResponse.from(savedUserTitle, title);
    }

    @Override
    @Transactional
    public UserTitleResponse equipTitle(Long titleId, User user) {
        UserTitle userTitle = titlePersistencePort.findByUserIdAndTitleId(user.getUserId(), titleId)
                .orElseThrow(() -> TitleNotOwnedException.INSTANCE);
        
        if (userTitle.isEquipped()) {
            return UserTitleResponse.from(userTitle, 
                    titlePersistencePort.findById(titleId)
                            .orElseThrow(() -> TitleNotFoundException.INSTANCE));
        }
        
        titlePersistencePort.unequipAllTitles(user.getUserId());
        
        userTitle.equip();
        UserTitle savedUserTitle = titlePersistencePort.save(userTitle);
        
        Title title = titlePersistencePort.findById(titleId)
                .orElseThrow(() -> TitleNotFoundException.INSTANCE);
        
        return UserTitleResponse.from(savedUserTitle, title);
    }

    @Override
    @Transactional
    public void unequipTitle(Long titleId, User user) {
        UserTitle userTitle = titlePersistencePort.findByUserIdAndTitleId(user.getUserId(), titleId)
                .orElseThrow(() -> TitleNotOwnedException.INSTANCE);
        
        if (!userTitle.isEquipped()) {
            return;
        }
        
        userTitle.unequip();
        titlePersistencePort.save(userTitle);
    }

    @Override
    @Transactional(readOnly = true)
    public UserTitleResponse getCurrentEquippedTitle(User user) {
        Optional<UserTitle> equippedTitle = titlePersistencePort.findByUserIdAndIsEquippedTrue(user.getUserId());
        
        if (equippedTitle.isEmpty()) {
            return null;
        }
        
        Title title = titlePersistencePort.findById(equippedTitle.get().getTitleId())
                .orElseThrow(() -> TitleNotFoundException.INSTANCE);
        
        return UserTitleResponse.from(equippedTitle.get(), title);
    }

    private void validateAdminRole(User user) {
        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("관리자만 칭호를 관리할 수 있습니다.");
        }
    }
}