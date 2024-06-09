package com.guroomsoft.icms.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@Alias("IdentifyVerification")
public class IdentifyVerification {
    @Schema(description = "PK")
    @JsonProperty("certSeq")
    private Long certSeq;

    @Schema(description = "계정")
    @JsonProperty("accountId")
    private String accountId;

    @Schema(description = "이메일")
    @JsonProperty("userEmail")
    private String userEmail;

    @Schema(description = "핸드폰번호")
    @JsonProperty("userPhone")
    private String userPhone;

    @Schema(description = "인증코드")
    @JsonProperty("certCode")
    private String certCode;

    @Schema(description = "유효시간")
    @JsonProperty("expiredDt")
    private String expiredDt;

    @Schema(description = "인증확인여부")
    @JsonProperty("checkAt")
    private String checkAt;
}
