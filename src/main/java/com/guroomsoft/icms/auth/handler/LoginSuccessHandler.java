package com.guroomsoft.icms.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guroomsoft.icms.auth.dto.JwtToken;
import com.guroomsoft.icms.auth.dto.PrincipalDetails;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.service.JwtService;
import com.guroomsoft.icms.auth.service.UserService;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.http.HttpReqRespUtils;
import com.guroomsoft.icms.util.MessageUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * 로그인 인증 성공 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final UserService userService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //1. 로그인 인증을 마친 사용자 가져오기
        User user = ((PrincipalDetails)authentication.getDetails()).getUser();

        //2. 토큰 생성
        JwtToken jwtToken = jwtService.issueToken(user.getUserUid(), user.getAccountId(), user.getAccountName(), user.extractAuthority());

        // 3. 인증 결과 반환
        user.setToken(jwtToken);

        // 4. 최근 로그인 기록
        userService.writeLastLogin(user, HttpReqRespUtils.getClientIpAddressIfServletRequestExist());

        DataResult<User> resultData = new DataResult<>();
        resultData.setResult(user);
        resultData.setCode(0);
        resultData.setType("info");
        resultData.setMessage(MessageUtil.getMessage("msg.auth.success"));

        String res = objectMapper.writeValueAsString(resultData);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(res);
    }
}
