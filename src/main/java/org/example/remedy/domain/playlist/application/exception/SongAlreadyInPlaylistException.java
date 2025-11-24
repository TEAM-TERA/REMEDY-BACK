package org.example.remedy.domain.playlist.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.AlreadyExistsException;

public class SongAlreadyInPlaylistException extends AlreadyExistsException {

    public static SongAlreadyInPlaylistException EXCEPTION = new SongAlreadyInPlaylistException();

    private SongAlreadyInPlaylistException() {
        super(ErrorCode.SONG_ALREADY_IN_PLAYLIST);
    }
}