package org.example.remedy.domain.song.dto.request;

import java.util.List;

public record SongCreateRequest(
        List<String> titles
) {
}
