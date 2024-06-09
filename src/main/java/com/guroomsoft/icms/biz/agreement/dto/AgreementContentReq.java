package com.guroomsoft.icms.biz.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Table Name:  전자계약데이터정보 요청
 */
@Data
public class AgreementContentReq {
    @Schema(description = "등록일") @JsonProperty("announceDate") private String announcedDate;
    @Schema(description = "플랜트코드") @JsonProperty("plantCd") private String plantCd;
    @Schema(description = "협력사코드목록") @JsonProperty("bpList") private List<String> bpList;
}
