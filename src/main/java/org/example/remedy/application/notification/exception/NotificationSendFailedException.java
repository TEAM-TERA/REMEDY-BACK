package org.example.remedy.application.notification.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class NotificationSendFailedException extends BusinessBaseException {

    public static NotificationSendFailedException EXCEPTION = new NotificationSendFailedException();

    private NotificationSendFailedException() {
        super(ErrorCode.NOTIFICATION_SEND_FAILED);
    }
}
