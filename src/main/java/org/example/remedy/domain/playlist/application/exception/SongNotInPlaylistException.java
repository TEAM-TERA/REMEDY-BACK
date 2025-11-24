package org.example.remedy.domain.playlist.application.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class SongNotInPlaylistException extends NotFoundException {

    public static SongNotInPlaylistException EXCEPTION = new SongNotInPlaylistException();

    private SongNotInPlaylistException() {
        super(ErrorCode.SONG_NOT_IN_PLAYLIST);
    }
}