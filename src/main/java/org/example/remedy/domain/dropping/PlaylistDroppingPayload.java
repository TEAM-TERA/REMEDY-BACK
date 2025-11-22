package org.example.remedy.domain.dropping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDroppingPayload implements Payload {

    private String playlistName;
    private List<String> songIds;
}