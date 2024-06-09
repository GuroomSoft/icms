package com.guroomsoft.icms.auth.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.auth.dto.RefreshToken;
import com.guroomsoft.icms.auth.provider.JwtProvider;
import com.guroomsoft.icms.common.exception.CTokenValidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 로그인, 토큰 재발급 시 사용되는 서비스
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final UserDAO userDAO;

    @Transactional(readOnly = true)
    public RefreshToken findRefreshToken(Long userUid) {
        try {
            return userDAO.selectRefreshToken(userUid);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Refresh token 저장
     * @param data
     * @return
     */
    @Transactional
    public RefreshToken save(Long userUid, RefreshToken data){
        try {
            RefreshToken token = userDAO.selectRefreshToken(userUid);
            if (token != null) {
                userDAO.updateRefreshToken(data);
            } else {
                data.setUserUid(userUid);
                userDAO.insertRefreshToken(data);
            }
            return userDAO.selectRefreshToken(data.getUserUid());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Refresh token 업데이트
     * @param userUid
     * @param refreshToken
     * @return
     */
    @Transactional
    public RefreshToken updateRefreshToken(Long userUid, String refreshToken){
        try {
            RefreshToken token = userDAO.selectRefreshToken(userUid);
            if (token != null) {
                token.setToken(refreshToken);
                userDAO.updateRefreshToken(token);
                return token;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // 사용자 리프레시 토큰과 일치하는지 검증
    public void validRefreshToken(Long useerUid, String refreshToken) {
        try {
            RefreshToken token = userDAO.selectRefreshToken(useerUid);
            if (token != null) {
                if (!refreshToken.equals(token.getToken())) {
                    throw new CTokenValidException();
                }
            } else {
                throw new CTokenValidException();
            }
        } catch (Exception e) {
            throw new CTokenValidException();
        }
    }


}
