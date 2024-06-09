package com.guroomsoft.icms.auth.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.auth.dto.JwtToken;
import com.guroomsoft.icms.auth.dto.RefreshToken;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * 로그인, 토큰 재발급 시 사용되는 서비스
 */
@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserDAO userDAO;

    // 토큰 생성 및 저장
    public JwtToken issueToken(Long userUid, String accountId, String accountNm, String role) {
        // 토큰 생성
        JwtToken newToken = jwtProvider.createJwtToken(userUid, accountId, accountNm, role);
        // 리프레시 토큰이 있는 사용자인지 없는 사용자인지 판별
        RefreshToken findRefreshToken = refreshTokenService.findRefreshToken(userUid);
        if (findRefreshToken == null){
            // 회원가입을 하고 처음 로그인하는 사용자 처리
            RefreshToken refreshToken = new RefreshToken(newToken.getRefreshToken());
            refreshTokenService.save(userUid, refreshToken);
        } else {
            // 기존 계정
            refreshTokenService.updateRefreshToken(userUid, newToken.getRefreshToken());
        }

        return newToken;
    }

    // Refresh token을 통해 accessToken 및 refreshToken 재발급
    public JwtToken reissue(String token) {
        // refresh token에서 user uid 추출
        Long userUid = jwtProvider.getUserUid(token);
        // 사용자 정보 조회
        try {
            User user = userDAO.selectUserByKey(userUid);
            // Access token 재성성
            String accessToken = jwtProvider.createAccessToken(
                    user.getUserUid(),
                    user.getAccountId(),
                    user.getAccountName(),
                    user.extractAuthority());

            return new JwtToken(accessToken, token);
        } catch (Exception e) {
            throw null;
        }
    }


}
