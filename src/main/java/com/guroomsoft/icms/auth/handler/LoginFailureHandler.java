package com.guroomsoft.icms.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.exception.CAuthenticationException;
import com.guroomsoft.icms.util.MessageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        CommonResult errorResult = new CommonResult();
        errorResult.setType("error");
        errorResult.setCode(CAuthenticationException.getCode());

        if(exception instanceof BadCredentialsException) {
            errorResult.setMessage(MessageUtil.getMessage("error.bad.credentials"));
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorResult.setMessage(MessageUtil.getMessage("error.internal.authentication"));
        } else if (exception instanceof UsernameNotFoundException) {
            // 사용자 ID를 동적으로 사용하기 위해 loadUserByUsername에서 만들어진 에러 메시지를 사용
            errorResult.setMessage(exception.getMessage());
        } else if (exception instanceof LockedException) {
            errorResult.setMessage(MessageUtil.getMessage("error.locked"));
        } else if (exception instanceof AccountExpiredException) {
            // 계정이 만료되었습니다.
            errorResult.setMessage(MessageUtil.getMessage("error.account.expired"));
        } else if (exception instanceof CredentialsExpiredException) {
            // 자격 증명 유효 기간이 만료되었습니다.
            errorResult.setMessage(MessageUtil.getMessage("error.credential.expired"));
        } else {
            errorResult.setMessage(MessageUtil.getMessage("error.authentication"));
        }
        String res = objectMapper.writeValueAsString(errorResult);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(res);
    }

    @Data
    private static class LoginDto {
        String username;
        String password;
    }

}
