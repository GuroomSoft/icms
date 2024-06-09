package com.guroomsoft.icms.auth.provider;

import com.guroomsoft.icms.auth.dto.PrincipalDetails;
import com.guroomsoft.icms.auth.service.PrincipalDetailsService;
import com.guroomsoft.icms.util.CipherUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GrAuthenticationProvider implements AuthenticationProvider {

    private final PrincipalDetailsService principalDetailsService;
    private UserDetailsChecker preAuthenticationChecks = new PreAuthenticationChecks();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("👉 [GrAuthenticationProvider] authenticate");
        String username = (String) authentication.getPrincipal();   // 사용자 ID
        String password = (String) authentication.getCredentials();   // 암호

        PrincipalDetails user = principalDetailsService.loadUserByUsername(username);
        // 사전 체크

        String encryptedPassword = CipherUtil.sha512encrypt(password);
        if (StringUtils.isBlank(password) || !encryptedPassword.equalsIgnoreCase(user.getPassword())) {
            log.info("👉 [GrAuthenticationProvider] 사용자 암호가 올바르지 않습니다.");
            throw new BadCredentialsException("사용자 암호가 유효하지 않습니다.");
        }
        try {
            this.preAuthenticationChecks.check(user);
        } catch (AuthenticationException ex) {
            throw ex;
        }

        log.info("👉 [GrAuthenticationProvider] 사용자 암호 확인");
        UsernamePasswordAuthenticationToken authenticationToken
                = new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        authenticationToken.setDetails(user);
        return authenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }

    private class PreAuthenticationChecks implements UserDetailsChecker {

        @Override
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                throw new LockedException("User account is locked");
            }
            if (!user.isEnabled()) {
                throw new DisabledException("User is disabled");
            }
            if (!user.isAccountNonExpired()) {
                throw new AccountExpiredException("User account has expired");
            }

        }
    }
}
