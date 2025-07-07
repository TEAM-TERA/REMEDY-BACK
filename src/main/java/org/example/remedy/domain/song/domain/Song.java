package org.example.remedy.domain.song.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collation = "song")
public class Song {
    @Id
    private String songId;

    private String title;

    private String artist;

    private String musicUrl;

    private Boolean age_checked;

    private Boolean is_certified;
}
