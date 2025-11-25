package org.example.remedy.domain.song.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
@Document(indexName = "songs")
public class Song {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;        // 노래 제목

    @Field(type = FieldType.Text, analyzer = "nori")
    private String artist;       // 아티스트

    @Field(type = FieldType.Integer)
    private int duration;    // 재생 시간 (초)

    @Field(type = FieldType.Keyword)
    private String albumImagePath;  // 앨범 이미지 경로

    @Builder
    public Song(String id, String title, String artist, int duration, String albumImagePath) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.albumImagePath = albumImagePath;
    }
}