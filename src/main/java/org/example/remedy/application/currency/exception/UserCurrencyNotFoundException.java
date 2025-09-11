package org.example.remedy.application.currency.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class UserCurrencyNotFoundException extends NotFoundException {
    public static final UserCurrencyNotFoundException INSTANCE = new UserCurrencyNotFoundException();

    private UserCurrencyNotFoundException() {
        super(ErrorCode.USER_CURRENCY_NOT_FOUND);
    }
}