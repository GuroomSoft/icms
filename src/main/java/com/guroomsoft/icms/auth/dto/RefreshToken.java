package com.guroomsoft.icms.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

/**
 * Table :
 *  ST_REFRESH_TOKEN
 * 사용자들의 Refresh token 을 User - Refresh token 일대일 관계로 DB 에서 관리
 */
@Data
@Alias("RefreshToken")
public class RefreshToken {
    @Schema(description = "Refresh token id")
    private Long tokenId;

    @Schema(description = "사용자 UID")
    @JsonProperty("userUid")
    private Long userUid;

    @Schema(description = "Refresh 토큰")
    @JsonProperty("refreshToken")
    private String token;

    private User user;

    // 추가 생성
    public RefreshToken(String refreshToken) { this.token = refreshToken; }

}
