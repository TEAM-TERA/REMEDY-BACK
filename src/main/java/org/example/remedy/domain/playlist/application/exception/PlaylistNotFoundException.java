package org.example.remedy.domain.playlist.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class PlaylistNotFoundException extends NotFoundException {

    public static PlaylistNotFoundException EXCEPTION = new PlaylistNotFoundException();

    private PlaylistNotFoundException() {
        super(ErrorCode.PLAYLIST_NOT_FOUND);
    }
}