package com.guroomsoft.icms.auth.dto;

import io.micrometer.common.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class PrincipalDetails implements UserDetails, Serializable {
    private static final long serialVersionUID = 174726374856728L;

    private User user;

    public PrincipalDetails(User _user) {
        this.user = _user;
    }
    /**
     * 사용자에게 부여 된 권한을 반환
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        List<UserRole> list = user.getAuthorities();
        list.stream().forEach(r -> {
            authorities.add(() -> r.getUserRole());
        });

        return authorities;
    }

    /**
     * 사용자를 인증하는데 사용되는 암호를 반환
     * @return
     */
    @Override
    public String getPassword() {
        return this.user.getAccountPwd();
    }

    /**
     * 사용자를 인증하는 사용되는 사용자 이름(아이디)를 반환
     * @return
     */
    @Override
    public String getUsername() {
        return this.user.getAccountId();
    }

    /**
     * 사용자 계정이 만료되었는지 여부
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        if (StringUtils.isBlank(this.user.getAccountExpiredDt())) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(dtf).compareTo(this.user.getAccountExpiredDt()) < 0;
    }

    /**
     * 사용자 계정이 잠겨 있는지를 여부를 반환
     * @return
     * true: 계정 사용 가능
     * false : 계정 잠금 상태
     */
    @Override
    public boolean isAccountNonLocked() {
        if (StringUtils.isNotBlank(this.user.getAccountLocked()) && this.user.getAccountLocked().equals("Y")) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 사용자 자격 증명(암호)이 만료되었는지 여부
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        if (StringUtils.isBlank(this.user.getPwdHashValue())) {
            return true;
        }

        LocalDateTime currentDatetime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return currentDatetime.format(dtf).compareTo(this.user.getPwdExpiredDt()) < 0;
    }

    /**
     * 사용자 사용 가능여부
     * @return
     */
    @Override
    public boolean isEnabled() {
        if (StringUtils.isBlank(this.user.getActive())) {
            return true;
        }

        if (this.user.getActive().equals("N")) {
            return false;
        }

        return true;
    }
}
