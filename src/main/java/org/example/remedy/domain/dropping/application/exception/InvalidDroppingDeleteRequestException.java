package org.example.remedy.domain.dropping.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class InvalidDroppingDeleteRequestException extends BusinessBaseException {
    public InvalidDroppingDeleteRequestException() {
        super(ErrorCode.INVALID_DROPPING_DELETE_REQUEST);
    }
}
