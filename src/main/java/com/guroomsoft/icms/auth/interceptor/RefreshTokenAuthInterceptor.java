package com.guroomsoft.icms.auth.interceptor;

import com.guroomsoft.icms.auth.provider.JwtProvider;
import com.guroomsoft.icms.auth.service.RefreshTokenService;
import com.guroomsoft.icms.common.exception.CTokenValidException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class RefreshTokenAuthInterceptor implements HandlerInterceptor {
    // 전달되는 Refresh token과 저장된 Refresh token이 일치하는지 검증
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("👉 [RefreshTokenAuthInterceptor] preHandle");

        // 1. Header에서 token 추출
        String token = getToken(request);

        // 2. Refresh token 유효성 검사
        if (!jwtProvider.validationToken(token)) throw new CTokenValidException();

        // 3. Reresh token Claim 검증
        Claims claims = jwtProvider.parseClaims(token);
        if (claims.get("userUid") == null) throw new CTokenValidException();

        Long userUid = Long.valueOf(claims.get("userUid").toString());

        // 4. DB 상에 Refresh token와 일치하는 검사
        refreshTokenService.validRefreshToken(userUid, token);

        return true;
    }

    // Request Header에서 Refresh token을 추출
    private String getToken(HttpServletRequest request){
        String token = request.getHeader(jwtProvider.REFRESH_HEADER_STRING);
        if (StringUtils.isBlank(token)) {
            throw new CTokenValidException();
        }
        return token;
    }
}
