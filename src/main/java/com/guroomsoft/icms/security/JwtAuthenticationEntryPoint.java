package com.guroomsoft.icms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.exception.CAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // JWT 토큰 인증이 실패하였을때 호출 되는 핸들러이다.
    // Spring Security 에서 인증되지 않은 사용자의 리소스에 대한 접근 처리는 AuthenticationEntryPoint 가 담당
    /* 401 UnAuthorized exception */
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("JWT 토큰 인증 실패");

        CommonResult errorResult = new CommonResult();
        errorResult.setType("error");
        errorResult.setCode(CAuthenticationException.getCode());
        errorResult.setMessage(CAuthenticationException.getCustomMessage());

        String res = objectMapper.writeValueAsString(errorResult);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(res);
    }
}
