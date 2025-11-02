package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * 곡 메타데이터 조회 책임
 * - Spotify 정보 조회
 * - 중복 체크 및 ID 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SongMetadataFetcher {

    private final SpotifyImageService spotifyImageService;
    private final SongPersistencePort songPersistencePort;

    public MetadataResult fetch(String requestTitle, String requestArtist) {
        SpotifyImageService.SpotifyAlbumImageResult spotify =
                spotifyImageService.searchAndDownloadAlbumImage(requestTitle, requestArtist);

        String finalTitle = spotify.isFound() ? spotify.getTrackName() : requestTitle;
        String finalArtist = spotify.isFound() ? spotify.getArtistName() : requestArtist;
        String albumImageUrl = spotify.isFound() ? spotify.getS3Url() : null;

        String songId = resolveSongId(finalTitle, finalArtist);

        return new MetadataResult(songId, finalTitle, finalArtist, albumImageUrl);
    }

    private String resolveSongId(String title, String artist) {
        Optional<Song> existingSong = songPersistencePort.findByTitleAndArtist(title, artist);

        if (existingSong.isPresent()) {
            String songId = existingSong.get().getId();
            log.info("중복 노래 발견 (덮어쓰기): {} by {} (ID: {})", title, artist, songId);
            return songId;
        }

        return UUID.randomUUID().toString();
    }

    public static class MetadataResult {
        private final String songId;
        private final String title;
        private final String artist;
        private final String albumImageUrl;

        public MetadataResult(String songId, String title, String artist, String albumImageUrl) {
            this.songId = songId;
            this.title = title;
            this.artist = artist;
            this.albumImageUrl = albumImageUrl;
        }

        public String getSongId() { return songId; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public String getAlbumImageUrl() { return albumImageUrl; }
    }
}
