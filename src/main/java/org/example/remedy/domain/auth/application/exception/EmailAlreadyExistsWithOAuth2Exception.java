package org.example.remedy.domain.auth.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class EmailAlreadyExistsWithOAuth2Exception extends BusinessBaseException {
    public static final BusinessBaseException EXCEPTION = new EmailAlreadyExistsWithOAuth2Exception();

    private EmailAlreadyExistsWithOAuth2Exception() {
        super(ErrorCode.EMAIL_ALREADY_EXISTS_WITH_OAUTH2);
    }
}
