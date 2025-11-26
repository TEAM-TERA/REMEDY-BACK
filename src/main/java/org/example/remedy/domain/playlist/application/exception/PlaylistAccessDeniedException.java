package org.example.remedy.domain.playlist.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.BusinessBaseException;

public class PlaylistAccessDeniedException extends BusinessBaseException {

    public static PlaylistAccessDeniedException EXCEPTION = new PlaylistAccessDeniedException();

    private PlaylistAccessDeniedException() {
        super(ErrorCode.PLAYLIST_ACCESS_DENIED);
    }
}