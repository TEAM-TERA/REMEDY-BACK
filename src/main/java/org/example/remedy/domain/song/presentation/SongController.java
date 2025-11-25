package org.example.remedy.domain.song.presentation;

import lombok.RequiredArgsConstructor;
import org.example.remedy.domain.song.application.service.SongService;
import org.example.remedy.domain.song.application.dto.response.SongListResponse;
import org.example.remedy.domain.song.application.dto.response.SongResponse;
import org.example.remedy.domain.song.application.dto.response.SongSearchListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자용 API 컨트롤러
 * 곡 추가, 삭제, 조회 기능 제공
 */
@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
public class SongController {
    private final SongService songService;

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
     * 특정 곡 삭제
     * DELETE /api/v1/songs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable String id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
}
