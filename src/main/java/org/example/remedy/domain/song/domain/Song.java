package org.example.remedy.domain.song.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.remedy.domain.song.dto.YouTubeMetadata;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "songs")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "songs")
public class Song {
    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String title;        // YouTube 영상 제목

    @Field(type = FieldType.Text, analyzer = "nori")
    private String artist;       // YouTube 채널명/아티스트

    @Field(type = FieldType.Integer)
    private Integer duration;    // 재생 시간 (초)

    @Field(type = FieldType.Keyword)
    private String hlsPath;      // HLS 플레이리스트 경로

    public static Song newInstance(YouTubeMetadata metadata, String hlsPath) {
        return new Song(
                metadata.getTitle(),
                metadata.getCleanArtist(),
                metadata.getDuration(),
                hlsPath
        );
    }
    public Song(String title, String artist, int duration, String hlsPath) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.hlsPath = hlsPath;
    }
}