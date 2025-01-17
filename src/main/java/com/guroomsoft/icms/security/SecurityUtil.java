package com.guroomsoft.icms.security;

import com.guroomsoft.icms.auth.dto.PrincipalDetails;
import com.guroomsoft.icms.auth.dto.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor
public class SecurityUtil {
    // Security Context 에 저장되어 있는 인증 객체 가져오기
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        User user = principal.getUser();

        return  user;
    }



}
