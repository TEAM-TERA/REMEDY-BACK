package org.example.remedy.interfaces.song;

import lombok.RequiredArgsConstructor;
import org.example.remedy.interfaces.song.dto.request.SongCreateRequest;
import org.example.remedy.interfaces.song.dto.response.SongListResponse;
import org.example.remedy.interfaces.song.dto.response.SongResponse;
import org.example.remedy.interfaces.song.dto.response.SongSearchListResponse;
import org.example.remedy.application.song.SongService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
     * 관리자가 노래 제목 입력 시 전체 프로세스 수행
     * Request Body: {"title": "좋은날"}
     */
    @PostMapping
    public ResponseEntity<SongListResponse> addSongs(@RequestBody SongCreateRequest request) {
        SongListResponse response = songService.createSongs(request);
        return ResponseEntity.ok(response);
    }

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

    @GetMapping("/{title}/stream")
    public ResponseEntity<Resource> streamMp3(@PathVariable String title) throws IOException {
        return songService.streamSong(title);
    }
}