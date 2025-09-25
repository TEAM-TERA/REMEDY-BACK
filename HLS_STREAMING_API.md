# HLS 스트리밍 API 명세서

## 개요
HTTP Live Streaming (HLS) 프로토콜을 사용하여 노래를 스트리밍하는 API입니다.

## Base URL
```
/api/v1/songs
```

---

## 1. HLS 플레이리스트 조회

### Endpoint
```http
GET /api/v1/songs/{songId}/stream
```

### Description
지정된 노래의 HLS 플레이리스트 파일(playlist.m3u8)을 반환합니다.

### Path Parameters
| Parameter | Type   | Required | Description |
|-----------|--------|----------|-------------|
| songId    | string | Yes      | 노래 고유 ID |

### Response Headers
| Header         | Value                         |
|----------------|-------------------------------|
| Content-Type   | application/vnd.apple.mpegurl |
| Cache-Control  | max-age=3600                  |

### Success Response
- **Code**: 200 OK
- **Content**: HLS 플레이리스트 파일 (.m3u8)

```
#EXTM3U
#EXT-X-VERSION:3
#EXT-X-TARGETDURATION:10
#EXT-X-MEDIA-SEQUENCE:0
#EXTINF:10.0,
segment000.ts
#EXTINF:10.0,
segment001.ts
#EXTINF:8.5,
segment002.ts
#EXT-X-ENDLIST
```

### Error Responses

#### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "곡을 찾을 수 없습니다",
  "path": "/api/v1/songs/{songId}/stream"
}
```

#### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "HLS 플레이리스트 파일을 찾을 수 없습니다: {songId}",
  "path": "/api/v1/songs/{songId}/stream"
}
```

### Example Request
```http
GET /api/v1/songs/abc123def456/stream HTTP/1.1
Host: api.example.com
```

---

## 2. HLS 세그먼트 파일 조회

### Endpoint
```http
GET /api/v1/songs/{songId}/segments/{segmentName}
```

### Description
HLS 플레이리스트에서 참조하는 세그먼트 파일(.ts)을 반환합니다.

### Path Parameters
| Parameter   | Type   | Required | Description                    |
|-------------|--------|----------|--------------------------------|
| songId      | string | Yes      | 노래 고유 ID                   |
| segmentName | string | Yes      | 세그먼트 파일명 (예: segment001.ts) |

### Segment Name Format
- 패턴: `segment{숫자}.ts`
- 예시: `segment000.ts`, `segment001.ts`, `segment010.ts`

### Response Headers
| Header         | Value         |
|----------------|---------------|
| Content-Type   | video/mp2t    |
| Cache-Control  | max-age=86400 |

### Success Response
- **Code**: 200 OK
- **Content**: HLS 세그먼트 파일 (바이너리 데이터)

### Error Responses

#### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "곡을 찾을 수 없습니다",
  "path": "/api/v1/songs/{songId}/segments/{segmentName}"
}
```

#### 400 Bad Request - 잘못된 파일명
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "유효하지 않은 세그먼트 파일명: {segmentName}",
  "path": "/api/v1/songs/{songId}/segments/{segmentName}"
}
```

#### 400 Bad Request - 파일 없음
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "HLS 세그먼트 파일을 찾을 수 없습니다: {segmentName}",
  "path": "/api/v1/songs/{songId}/segments/{segmentName}"
}
```

### Example Request
```http
GET /api/v1/songs/abc123def456/segments/segment001.ts HTTP/1.1
Host: api.example.com
```

---

## 사용 시나리오

### 1. 기본 HLS 스트리밍 플로우

1. **플레이리스트 요청**
   ```http
   GET /api/v1/songs/{songId}/stream
   ```

2. **플레이리스트 응답 처리**
   - 클라이언트가 .m3u8 파일을 파싱
   - 세그먼트 목록 추출

3. **세그먼트 요청**
   ```http
   GET /api/v1/songs/{songId}/segments/segment000.ts
   GET /api/v1/songs/{songId}/segments/segment001.ts
   GET /api/v1/songs/{songId}/segments/segment002.ts
   ...
   ```

### 2. JavaScript HLS 플레이어 예시

```javascript
// HLS.js 사용 예시
const video = document.getElementById('video');
const songId = 'abc123def456';

if (Hls.isSupported()) {
  const hls = new Hls();
  hls.loadSource(`/api/v1/songs/${songId}/stream`);
  hls.attachMedia(video);

  hls.on(Hls.Events.MANIFEST_PARSED, function() {
    video.play();
  });
}
```

---

## 보안 고려사항

### 세그먼트 파일명 검증
- 파일명은 `segment\d+\.ts` 패턴만 허용
- Path Traversal 공격 방지
- 잘못된 파일 접근 차단

### 캐시 정책
- **플레이리스트**: 1시간 캐시 (`max-age=3600`)
- **세그먼트**: 24시간 캐시 (`max-age=86400`)

### 인증 및 권한
- 현재 구현에서는 인증 없이 접근 가능
- 필요시 JWT 토큰 기반 인증 추가 가능

---

## 에러 처리

모든 API는 일관된 에러 응답 형식을 사용합니다:

```json
{
  "timestamp": "ISO-8601 형식",
  "status": "HTTP 상태 코드",
  "error": "에러 타입",
  "message": "에러 메시지",
  "path": "요청 경로"
}
```

---

## 성능 최적화

### 캐시 전략
- CDN 활용 권장
- 브라우저 캐시 활용
- 서버 사이드 캐시 구현

### 네트워크 최적화
- HTTP/2 지원
- gzip 압축 활용
- 적절한 세그먼트 길이 설정 (기본 10초)

---

## 호환성

### 지원 브라우저
- Chrome 34+
- Firefox 42+
- Safari 8+
- Edge 13+

### 지원 플랫폼
- iOS Safari (네이티브 HLS 지원)
- Android Chrome (HLS.js 필요)
- 데스크톱 브라우저 (HLS.js 필요)