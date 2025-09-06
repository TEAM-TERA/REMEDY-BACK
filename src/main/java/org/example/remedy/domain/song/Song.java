package org.example.remedy.domain.song;

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
    private String title;        // YouTube 영상 제목

    @Field(type = FieldType.Text, analyzer = "nori")
    private String artist;       // YouTube 채널명/아티스트

    @Field(type = FieldType.Integer)
    private int duration;    // 재생 시간 (초)

    @Field(type = FieldType.Keyword)
    private String hlsPath;      // HLS 플레이리스트 경로
}