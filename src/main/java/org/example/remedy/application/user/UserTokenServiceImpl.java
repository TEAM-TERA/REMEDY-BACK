package org.example.remedy.application.user;

import lombok.RequiredArgsConstructor;
import org.example.remedy.application.user.exception.UserNotFoundException;
import org.example.remedy.application.user.port.in.UserTokenService;
import org.example.remedy.application.user.port.out.UserPersistencePort;
import org.example.remedy.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserTokenServiceImpl implements UserTokenService {

    private final UserPersistencePort userPersistencePort;

    @Override
    public String findTokenByUserId(Long userId) {
        User user = userPersistencePort.findByUserId(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        return user.getFcmToken();
    }

    @Override
    @Transactional
    public void saveToken(Long userId, String token) {
        User user = userPersistencePort.findByUserId(userId)
                .orElseThrow(() -> UserNotFoundException.EXCEPTION);

        if (isSameToken(user.getFcmToken(), token)) {
            return;
        }

        user.updateFcmToken(token);
        userPersistencePort.save(user);
    }

    private boolean isSameToken(String existingToken, String newToken) {
        if (existingToken == null) {
            return newToken == null;
        }
        return existingToken.equals(newToken);
    }
}
