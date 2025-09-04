package org.example.remedy.interfaces.song.dto.request;

import java.util.List;

public record SongCreateRequest(
        List<String> titles
) {
}
