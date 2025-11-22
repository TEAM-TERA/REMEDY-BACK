package org.example.remedy.domain.auth.application.mapper;

import org.example.remedy.domain.auth.application.dto.request.AuthRegisterRequest;
import org.example.remedy.domain.user.domain.User;

public class AuthMapper {
  public static User toEntity(AuthRegisterRequest request, String password) {
    return new User(
        request.username(), password, request.email(), request.birthDate(), request.gender());
  }
}
