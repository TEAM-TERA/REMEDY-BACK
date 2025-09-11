package org.example.remedy.application.title.port.in;

import org.example.remedy.application.title.dto.response.TitleListResponse;
import org.example.remedy.application.title.dto.response.TitleResponse;
import org.example.remedy.application.title.dto.response.UserTitleListResponse;
import org.example.remedy.application.title.dto.response.UserTitleResponse;
import org.example.remedy.domain.user.User;

/**
 * 칭호 서비스 인터페이스
 * 
 * 칭호 관리, 사용자 칭호 관리 및 칭호 거래 기능을 정의합니다.
 */
public interface TitleService {
    /**
     * 새로운 칭호 생성
     * 
     * @param name 칭호 이름
     * @param description 칭호 설명
     * @param price 칭호 가격
     * @param admin 관리자 정보
     * @return 생성된 칭호 정보
     */
    TitleResponse createTitle(String name, String description, Integer price, User admin);
    
    /**
     * 모든 칭호 목록 조회 (관리자용)
     * 
     * @return 전체 칭호 목록 (비활성화된 칭호 포함)
     */
    TitleListResponse getAllTitles();
    
    /**
     * 활성 칭호 목록 조회 (사용자용)
     * 
     * @return 활성화된 칭호 목록
     */
    TitleListResponse getActiveTitles();
    
    /**
     * 칭호 정보 수정
     * 
     * @param titleId 수정할 칭호 ID
     * @param name 새로운 칭호 이름
     * @param description 새로운 칭호 설명
     * @param price 새로운 칭호 가격
     * @param admin 관리자 정보
     * @return 수정된 칭호 정보
     */
    TitleResponse updateTitle(Long titleId, String name, String description, Integer price, User admin);
    
    /**
     * 칭호 비활성화
     * 
     * @param titleId 비활성화할 칭호 ID
     * @param admin 관리자 정보
     */
    void deactivateTitle(Long titleId, User admin);
    
    /**
     * 칭호 활성화
     * 
     * @param titleId 활성화할 칭호 ID
     * @param admin 관리자 정보
     */
    void activateTitle(Long titleId, User admin);
    
    /**
     * 사용자 보유 칭호 목록 조회
     * 
     * @param user 사용자 정보
     * @return 사용자가 보유한 칭호 목록
     */
    UserTitleListResponse getUserTitles(User user);
    
    /**
     * 칭호 구매
     * 
     * @param titleId 구매할 칭호 ID
     * @param user 구매자 정보
     * @return 구매된 칭호 정보
     */
    UserTitleResponse purchaseTitle(Long titleId, User user);
    
    /**
     * 칭호 착용
     * 
     * @param titleId 착용할 칭호 ID
     * @param user 사용자 정보
     * @return 착용된 칭호 정보
     */
    UserTitleResponse equipTitle(Long titleId, User user);
    
    /**
     * 칭호 해제
     * 
     * @param titleId 해제할 칭호 ID
     * @param user 사용자 정보
     */
    void unequipTitle(Long titleId, User user);
    
    /**
     * 현재 착용 중인 칭호 조회
     * 
     * @param user 사용자 정보
     * @return 현재 착용 중인 칭호 정보
     */
    UserTitleResponse getCurrentEquippedTitle(User user);
}