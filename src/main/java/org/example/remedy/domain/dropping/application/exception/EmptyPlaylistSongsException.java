package org.example.remedy.domain.dropping.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class EmptyPlaylistSongsException extends BusinessBaseException {

    public static EmptyPlaylistSongsException EXCEPTION = new EmptyPlaylistSongsException();

    private EmptyPlaylistSongsException() {
        super(ErrorCode.EMPTY_PLAYLIST_SONGS);
    }
}
