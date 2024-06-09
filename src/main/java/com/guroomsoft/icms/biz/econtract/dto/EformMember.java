package com.guroomsoft.icms.biz.econtract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Schema(description = "MT_EFORM_MEMBER - 이폼사인 멤버")
@Data
@Alias("eformMember")
public class EformMember {
    @Schema(description = "efId")
    @JsonProperty("efId")
    private String efId;

    @Schema(description = "accountId")
    @JsonProperty("accountId")
    private String accountId;

    @Schema(description = "efName")
    @JsonProperty("efName")
    private String efName;

    @Schema(description = "초대거절여부")
    @JsonProperty("isRefused")
    private String isRefused;

    @Schema(description = "초대여부")
    @JsonProperty("isInvited")
    private String isInvited;

    @Schema(description = "탈퇴여부")
    @JsonProperty("isWithdrawal")
    private String isWithdrawal;

    @Schema(description = "초대만료여부")
    @JsonProperty("isExpired")
    private String isExpired;

    @Schema(description = "탈퇴여부")
    @JsonProperty("isDeleted")
    private String isDeleted;

    @Schema(description = "활성화 여부")
    @JsonProperty("isEnabled")
    private String isEnabled;

}
