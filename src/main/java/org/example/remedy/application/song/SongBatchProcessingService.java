package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.dto.response.SongDownloadResponse;
import org.example.remedy.application.song.port.out.SongPersistencePort;
import org.example.remedy.domain.song.Song;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongBatchProcessingService {

    private final YouTubeDownloadService youTubeDownloadService;
    private final SpotifyImageService spotifyImageService;
    private final HLSService hlsService;
    private final SongPersistencePort songPersistencePort;

    public List<SongDownloadResponse> processSongBatch(List<String> songTitles) {
        log.info("노래 일괄 처리 시작: {}곡 (순차 처리)", songTitles.size());
        long start = System.currentTimeMillis();

        // Spotify 토큰 사전 확보
        try {
            spotifyImageService.ensureValidToken();
        } catch (Exception e) {
            log.warn("Spotify 토큰 확보 실패: {}", e.getMessage());
        }

        List<SongDownloadResponse> results = new ArrayList<>();

        // 순차 처리
        for (String songTitle : songTitles) {
            try {
                SongDownloadResponse response = processSingleSong(songTitle);
                results.add(response);
            } catch (Exception e) {
                log.error("노래 처리 실패: {}", songTitle, e);
                results.add(SongDownloadResponse.failure(songTitle, e.getMessage()));
            }
        }

        log.info("노래 일괄 처리 완료: {}곡, 소요시간: {}ms",
                results.size(), System.currentTimeMillis() - start);
        return results;
    }

    private SongDownloadResponse processSingleSong(String songTitle) {
        long songStart = System.currentTimeMillis();

        try {
            // 1단계: Spotify에서 정확한 곡 정보 조회
            SpotifyImageService.SpotifyAlbumImageResult spotify =
                    spotifyImageService.searchAndDownloadAlbumImage(songTitle, "");

            String finalTitle = spotify.isFound() ? spotify.getTrackName() : songTitle;
            String finalArtist = spotify.isFound() ? spotify.getArtistName() : "";

            // 2단계: 중복 체크 - 제목과 아티스트로 검색
            Optional<Song> existingSong = songPersistencePort.findByTitleAndArtist(finalTitle, finalArtist);

            if (existingSong.isPresent()) {
                Song existing = existingSong.get();
                log.info("중복 노래 발견 (스킵): {} by {} (ID: {}), 소요시간: {}ms",
                        existing.getTitle(), existing.getArtist(), existing.getId(),
                        System.currentTimeMillis() - songStart);

                return SongDownloadResponse.success(existing.getId(), existing.getTitle(),
                        existing.getArtist(), existing.getHlsPath(), existing.getAlbumImagePath());
            }

            // 3단계: YouTube 다운로드 (중복이 아닌 경우에만)
            String searchQuery = finalTitle + (finalArtist.isEmpty() ? "" : " " + finalArtist) + " audio";
            YouTubeDownloadService.YouTubeSearchResult youtube =
                    youTubeDownloadService.searchAndDownload(searchQuery);

            // 4단계: HLS 변환
            String songId = UUID.randomUUID().toString();
            String hlsPath = hlsService.convertToHLS(youtube.getDownloadPath(), songId);

            // 5단계: DB 저장
            Song song = Song.builder()
                    .id(songId)
                    .title(finalTitle)
                    .artist(finalArtist)
                    .duration(youtube.getDuration())
                    .hlsPath(hlsPath)
                    .albumImagePath(spotify.isFound() ? spotify.getLocalPath() : null)
                    .build();

            Song saved = songPersistencePort.save(song);

            log.info("노래 처리 완료: {} by {} (ID: {}), 소요시간: {}ms",
                    saved.getTitle(), saved.getArtist(), saved.getId(),
                    System.currentTimeMillis() - songStart);

            return SongDownloadResponse.success(saved.getId(), saved.getTitle(),
                    saved.getArtist(), saved.getHlsPath(), saved.getAlbumImagePath());

        } catch (Exception e) {
            log.error("노래 처리 실패: {}, 소요시간: {}ms", songTitle,
                    System.currentTimeMillis() - songStart, e);
            return SongDownloadResponse.failure(songTitle, e.getMessage());
        }
    }
}