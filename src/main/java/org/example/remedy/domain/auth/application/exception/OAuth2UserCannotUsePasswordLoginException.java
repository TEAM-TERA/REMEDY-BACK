package org.example.remedy.domain.auth.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class OAuth2UserCannotUsePasswordLoginException extends BusinessBaseException {
    public static final BusinessBaseException EXCEPTION = new OAuth2UserCannotUsePasswordLoginException();

    private OAuth2UserCannotUsePasswordLoginException() {
        super(ErrorCode.OAUTH2_USER_CANNOT_USE_PASSWORD_LOGIN);
    }
}
