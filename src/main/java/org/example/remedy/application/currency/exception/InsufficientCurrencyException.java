package org.example.remedy.application.currency.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class InsufficientCurrencyException extends BusinessBaseException {
    public static final InsufficientCurrencyException INSTANCE = new InsufficientCurrencyException();

    private InsufficientCurrencyException() {
        super(ErrorCode.INSUFFICIENT_CURRENCY);
    }
}