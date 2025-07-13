package org.example.remedy.domain.song.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * YouTube 메타데이터 DTO
 * yt-dlp에서 추출한 정보를 담는 객체
 */
@Getter
@NoArgsConstructor
public class YouTubeMetadata {
    private String title;        // 영상 제목
    private String uploader;     // 채널명
    private Integer duration;    // 재생 시간 (초)

    public YouTubeMetadata(String title, String uploader, Integer duration) {
        this.title = title;
        this.uploader = uploader;
        this.duration = duration;
    }

    public static YouTubeMetadata newInstance(JsonNode jsonNode) {
        return new YouTubeMetadata(
                jsonNode.path("title").asText(),
                jsonNode.path("uploader").asText(),
                jsonNode.path("duration").asInt()
        );
    }

    /**
     * 아티스트명 정제
     * "Artist - Topic" → "Artist"
     * "Artist Official" → "Artist"
     */
    public String getCleanArtist() {
        if (uploader == null) return "Unknown Artist";

        String cleaned = uploader;
        if (cleaned.endsWith(" - Topic")) {
            cleaned = cleaned.substring(0, cleaned.length() - " - Topic".length());
        }
        if (cleaned.endsWith(" Official")) {
            cleaned = cleaned.substring(0, cleaned.length() - " Official".length());
        }
        if (cleaned.endsWith("VEVO")) {
            cleaned = cleaned.substring(0, cleaned.length() - "VEVO".length()).trim();
        }

        return cleaned.isEmpty() ? "Unknown Artist" : cleaned;
    }

    @Override
    public String toString() {
        return "YouTubeMetadata{" +
                "title='" + title + '\'' +
                ", uploader='" + uploader + '\'' +
                ", duration=" + duration +
                '}';
    }
}