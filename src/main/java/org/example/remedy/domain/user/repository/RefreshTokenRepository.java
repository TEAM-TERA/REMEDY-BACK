package org.example.remedy.domain.user.repository;

import org.example.remedy.domain.user.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
