package org.example.remedy.domain.user.domain;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.index.Indexed;

public class RefreshToken {
    @Id
    private String email;

    @Indexed
    private String refreshToken;

    public RefreshToken(String email, String refreshToken) {
        this.email = email;
        this.refreshToken = refreshToken;
    }
}
