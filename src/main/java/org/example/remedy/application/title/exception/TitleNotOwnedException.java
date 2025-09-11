package org.example.remedy.application.title.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class TitleNotOwnedException extends BusinessBaseException {
    public static final TitleNotOwnedException INSTANCE = new TitleNotOwnedException();

    private TitleNotOwnedException() {
        super(ErrorCode.TITLE_NOT_OWNED);
    }
}