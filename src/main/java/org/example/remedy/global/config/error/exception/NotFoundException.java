package org.example.remedy.global.config.error.exception;

import org.example.remedy.global.config.error.ErrorCode;

public class NotFoundException extends BusinessBaseException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
