package com.guroomsoft.icms.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.biz.code.dto.Corporation;
import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("user")
public class User extends BaseDTO {
    @Schema(description = "사용자 UID")
    @JsonProperty("userUid")
    private Long userUid;

    @Schema(description = "로그인 계정 ID")
    @JsonProperty("accountId")
    private String accountId;

    @Schema(description = "로그인 계정명")
    @JsonProperty("accountName")
    private String accountName;

    @Schema(description = "암호")
    @JsonProperty("accountPwd")
    private String accountPwd;

    @Schema(description = "별명")
    @JsonProperty("accountNickname")
    private String accountNickname;

    @Schema(description = "계정유형")
    @JsonProperty("accountType")
    private String accountType;

    @Schema(description = "계정유형명")
    @JsonProperty("accountTypeNm")
    private String accountTypeNm;

    @Schema(description = "초기화 암호 해시값")
    @JsonProperty("pwdHashValue")
    private String pwdHashValue;

    @Schema(description = "암호만료일시")
    @JsonProperty("pwdExpiredDt")
    private String pwdExpiredDt;

    @Schema(description = "활성화여부(Y/N)")
    @JsonProperty("active")
    private String active;

    @Schema(description = "이메일주소")
    @JsonProperty("email")
    private String email;

    @Schema(description = "메일주소 검증여부(Y/N)")
    @JsonProperty("verifiedEmail")
    private String verifiedEmail;

    @Schema(description = "사용자 핸드폰번호")
    @JsonProperty("mobile")
    private String mobile;

    @Schema(description = "핸드폰 검증여부(Y/N)")
    @JsonProperty("verifiedMobile")
    private String verifiedMobile;

    @Schema(description = "계정잠금 여부(Y/N)")
    @JsonProperty("accountLocked")
    private String accountLocked;

    @Schema(description = "계정만료일시")
    @JsonProperty("accountExpiredDt")
    private String accountExpiredDt;

    @Schema(description = "사용자 전화번호")
    @JsonProperty("accountTel")
    private String accountTel;

    @Schema(description = "최근 로그인 IP")
    @JsonProperty("lastLoginIp")
    private String lastLoginIp;

    @Schema(description = "최근 로그인 일시")
    @JsonProperty("lastLoginDt")
    private String lastLoginDt;

    @Schema(description = "회사코드")
    @JsonProperty("corpCd")
    private String corpCd;

    @Schema(description = "회사명")
    @JsonProperty("corpNm")
    private String corpNm;

    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "플랜트상세정보")
    @JsonProperty("corporationDetail")
    private Corporation corporationDetail;

    @Schema(description = "플랜트상세정보")
    @JsonProperty("plantDetail")
    private Plant plantDetail;

    @Schema(description = "파트너 상세정보")
    @JsonProperty("partnerDetail")
    private Partner partnerDetail;

    @Schema(description = "플랜트명")
    @JsonProperty("plantNm")
    private String plantNm;

    @Schema(description = "부서코드")
    @JsonProperty("orgCd")
    private String orgCd;

    @Schema(description = "부서명")
    @JsonProperty("orgNm")
    private String orgNm;

    @Schema(description = "팀코드")
    @JsonProperty("teamCd")
    private String teamCd;

    @Schema(description = "팀명")
    @JsonProperty("teamNm")
    private String teamNm;

    @Schema(description = "협력사 코드")
    @JsonProperty("bpCd")
    private String bpCd;

    @Schema(description = "협력사명")
    @JsonProperty("bpNm")
    private String bpNm;

    @Schema(description = "사용자 서명")
    @JsonProperty("userSign")
    private String userSign;

    @Schema(description = "Eform 사용 가능여부")
    @JsonProperty("eformIsEnabled")
    private String eformIsEnabled;

    @Schema(description = "사용자 그룹 UID")
    @JsonProperty("grpUid")
    private Long grpUid;

    @Schema(description = "사용자 Roles")
    @JsonProperty("authorities")
    private List<UserRole> authorities;

    @Schema(description = "JWT Token")
    @JsonProperty("token")
    private JwtToken token;

    public String extractAuthority() {
        if (authorities != null && !authorities.isEmpty()) {
            return authorities.get(0).getUserRole();
        } else {
            return "";
        }
    }

    @Schema(description = "회사 직인")
    @JsonProperty("corporateSeal")
    private String corporateSeal;
}
