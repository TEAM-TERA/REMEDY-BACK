package org.example.remedy.domain.dropping.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class UnauthorizedPlaylistAccessException extends BusinessBaseException {

    public static UnauthorizedPlaylistAccessException EXCEPTION = new UnauthorizedPlaylistAccessException();

    private UnauthorizedPlaylistAccessException() {
        super(ErrorCode.UNAUTHORIZED_PLAYLIST_ACCESS);
    }
}
