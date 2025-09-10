package org.example.remedy.application.song.exception;

import org.example.remedy.global.error.ErrorCode;
import org.example.remedy.global.error.exception.NotFoundException;

public class SongNotFoundException extends NotFoundException {

    public static SongNotFoundException EXCEPTION = new SongNotFoundException();

    private SongNotFoundException() {
        super(ErrorCode.SONG_NOT_FOUND);
    }
}
