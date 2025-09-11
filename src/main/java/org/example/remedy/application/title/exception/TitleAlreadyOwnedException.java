package org.example.remedy.application.title.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class TitleAlreadyOwnedException extends BusinessBaseException {
    public static final TitleAlreadyOwnedException INSTANCE = new TitleAlreadyOwnedException();

    private TitleAlreadyOwnedException() {
        super(ErrorCode.TITLE_ALREADY_OWNED);
    }
}