package com.guroomsoft.icms.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.exception.CAuthorityException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAcessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    // JWT토큰의 인가 실패시 호출되는 핸들러
    //Spring Security 에서 인증이 되었지만 권한이 없는 사용자의 리소스에 대한 접근 처리는 AccessDeniedHandler 가 담당
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException
    {
        log.error("권한이 없는 사용자 접근");
        CommonResult errorResult = new CommonResult();
        errorResult.setCode(CAuthorityException.getCode());
        errorResult.setMessage(CAuthorityException.getCustomMessage());
        errorResult.setType("error");

        String res = objectMapper.writeValueAsString(errorResult);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.getWriter().println(res);
    }
}
