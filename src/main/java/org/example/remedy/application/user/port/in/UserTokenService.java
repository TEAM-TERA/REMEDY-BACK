package org.example.remedy.application.user.port.in;

public interface UserTokenService {
    String findTokenByUserId(Long userId);
    void saveToken(Long userId, String token);
}
