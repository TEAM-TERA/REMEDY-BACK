package org.example.remedy.presentation.song.dto.request;

import java.util.List;

public record SongCreateRequest(
        List<String> titles
) {
}
