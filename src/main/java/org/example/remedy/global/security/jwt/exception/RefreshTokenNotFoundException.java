package org.example.remedy.global.security.jwt.exception;

import org.example.remedy.global.config.error.ErrorCode;
import org.example.remedy.global.config.error.exception.BusinessBaseException;

public class RefreshTokenNotFoundException extends BusinessBaseException {
    public static final BusinessBaseException EXCEPTION =
            new RefreshTokenNotFoundException();

    private RefreshTokenNotFoundException() { super(ErrorCode.REFRESH_TOKEN_NOT_FOUND); }
}