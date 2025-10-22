package org.example.remedy.application.notification.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class FcmTokenNotFoundException extends NotFoundException {

    public static FcmTokenNotFoundException EXCEPTION = new FcmTokenNotFoundException();

    private FcmTokenNotFoundException() {
        super(ErrorCode.FCM_TOKEN_NOT_FOUND);
    }
}
