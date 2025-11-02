package org.example.remedy.application.song;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remedy.application.song.dto.SongProcessingResult;
import org.example.remedy.presentation.song.dto.request.SongDownloadRequest;
import org.springframework.stereotype.Component;

/**
 * 곡 다운로드 오케스트레이션 책임
 * - 메타데이터 조회 + YouTube 다운로드 조합
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SongDownloadOrchestrator {
    private final SongMetadataFetcher metadataFetcher;
    private final YouTubeDownloadService youTubeDownloadService;

    public SongProcessingResult downloadSong(SongDownloadRequest request) {
        try {
            SongMetadataFetcher.MetadataResult metadata =
                    metadataFetcher.fetch(request.songTitle(), request.artist());

            String searchQuery = buildSearchQuery(metadata.getTitle(), metadata.getArtist());
            YouTubeDownloadService.YouTubeSearchResult youtube =
                    youTubeDownloadService.searchAndDownload(searchQuery);

            log.info("다운로드 완료: {} by {} -> {}",
                    metadata.getTitle(), metadata.getArtist(), youtube.getDownloadPath());

            return buildSuccessResult(metadata, youtube);
        } catch (Exception e) {
            log.error("노래 다운로드 실패: {} by {}", request.songTitle(), request.artist(), e);
            return SongProcessingResult.failed(request.songTitle(), request.artist(), e.getMessage());
        }
    }

    private String buildSearchQuery(String title, String artist) {
        return title + (artist.isEmpty() ? "" : " " + artist) + " audio";
    }

    private SongProcessingResult buildSuccessResult(
            SongMetadataFetcher.MetadataResult metadata,
            YouTubeDownloadService.YouTubeSearchResult youtube) {

        return SongProcessingResult.builder()
                .songId(metadata.getSongId())
                .title(metadata.getTitle())
                .artist(metadata.getArtist())
                .duration(youtube.getDuration())
                .mp3LocalPath(youtube.getDownloadPath())
                .albumImageS3Url(metadata.getAlbumImageUrl())
                .success(true)
                .build();
    }
}
