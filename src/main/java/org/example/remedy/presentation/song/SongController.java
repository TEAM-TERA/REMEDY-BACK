package org.example.remedy.presentation.song;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.remedy.application.song.SongBatchProcessingService;
import org.example.remedy.application.song.SongServiceImpl;
import org.example.remedy.application.song.dto.response.*;
import org.example.remedy.presentation.song.dto.request.SongBatchDownloadRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 관리자용 API 컨트롤러
 * 곡 추가, 삭제, 조회 기능 제공
 */
@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongServiceImpl songService;
    private final SongBatchProcessingService songBatchProcessingService;

    /**
     * 모든 곡 목록 조회
     * GET /api/v1/songs
     */
    @GetMapping
    public ResponseEntity<SongListResponse> getAllSongs() {
        SongListResponse response = songService.getAllSongs();
        return ResponseEntity.ok(response);
    }

    /**
     *
     * 노래 통합 검색(제목, 가수 섞여도 문제 X)
     * GET /api/v1/songs/search?query=아이유
     */
    @GetMapping("/search")
    public ResponseEntity<SongSearchListResponse> searchSongs(@RequestParam String query) {
    SongSearchListResponse response = songService.searchSongs(query);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 곡 정보 조회
     * GET /api/v1/songs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SongResponse> getSongById(@PathVariable String id) {
        SongResponse song = songService.getSongById(id);
        return ResponseEntity.ok(song);
    }

    /**
     * HLS 스트리밍 (플레이리스트 제공)
     * GET /api/v1/songs/{songId}/stream
     */
    @GetMapping("/{songId}/stream")
    public ResponseEntity<Resource> streamHLS(@PathVariable String songId) throws IOException {
        return songService.streamHLS(songId);
    }

    /**
     * HLS 세그먼트 파일 제공
     * GET /api/v1/songs/{songId}/segments/{segmentName}
     */
    @GetMapping("/{songId}/segments/{segmentName}")
    public ResponseEntity<Resource> getHLSSegment(
            @PathVariable String songId,
            @PathVariable String segmentName) throws IOException {
        return songService.getHLSSegment(songId, segmentName);
    }

    /**
     * 노래 일괄 처리 API
     * POST /api/v1/songs/batch-process
     * 노래 제목 리스트를 받아서 YouTube 다운로드, Spotify 앨범 이미지, HLS 변환 후 저장
     */
    @PostMapping("/batch-process")
    public ResponseEntity<SongBatchDownloadResponse> processSongBatch(
            @Valid @RequestBody SongBatchDownloadRequest request) {

        List<SongDownloadResponse> results =
            songBatchProcessingService.processSongBatch(request.getSongTitles());

        SongBatchDownloadResponse response = SongBatchDownloadResponse.from(results);
        return ResponseEntity.ok(response);
    }
}