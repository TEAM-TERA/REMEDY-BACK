package org.example.remedy.domain.dropping.dto.response;

import org.example.remedy.domain.dropping.domain.Dropping;
import java.time.LocalDateTime;

public record DroppingFindResponse(
        String droppingId,
        String songId,
        Long userId,
        String content,
        LocalDateTime expiryDate,
        LocalDateTime createdAt
) {
    /**
     * Dropping 도메인 객체로부터 DroppingFindResponse 인스턴스를 생성합니다.
     *
     * @param dropping 변환할 Dropping 도메인 객체
     * @return Dropping 객체의 정보를 담은 DroppingFindResponse 인스턴스
     */
    public static DroppingFindResponse newInstance(Dropping dropping) {
        return new DroppingFindResponse(
                dropping.getDroppingId(),
                dropping.getSongId(),
                dropping.getUserId(),
                dropping.getContent(),
                dropping.getExpiryDate(),
                dropping.getCreatedAt()
        );
    }
}