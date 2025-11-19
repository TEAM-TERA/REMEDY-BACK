package org.example.remedy.application.auth.port.in;

import jakarta.servlet.http.HttpServletResponse;
import org.example.remedy.presentation.auth.dto.AuthLoginRequest;
import org.example.remedy.presentation.auth.dto.AuthRegisterRequest;

public interface AuthService {

    void signup (AuthRegisterRequest req);

    void login(AuthLoginRequest req, HttpServletResponse res);
}
