package org.example.remedy.global.error.exception;

import org.example.remedy.global.error.ErrorCode;

public class NotFoundException extends BusinessBaseException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
