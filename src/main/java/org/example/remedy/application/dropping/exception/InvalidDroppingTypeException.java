package org.example.remedy.application.dropping.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class InvalidDroppingTypeException extends BusinessBaseException {

    public static final InvalidDroppingTypeException EXCEPTION = new InvalidDroppingTypeException();

    private InvalidDroppingTypeException() {
        super(ErrorCode.INVALID_DROPPING_TYPE);
    }
}