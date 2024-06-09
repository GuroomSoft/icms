package com.guroomsoft.icms.biz.price.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * Table Name:  가격변경 문서 정보
 *  BT_PRICE_CHANGE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("priceChange")
public class PriceChange extends BaseDTO {
    @Schema(description = "문서번호")
    @JsonProperty("docNo")
    private String docNo;

    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "협력사 코드")
    @JsonProperty("bpCd")
    private String bpCd;

    @Schema(description = "공급업체명")
    @JsonProperty("bpNm")
    private String bpNm;

    @Schema(description = "공시단가 등록일")
    @JsonProperty("announcedDate")
    private String announcedDate;

    @Schema(description = "합의일자")
    @JsonProperty("agreementDate")
    private String agreementDate;

    @Schema(description = "부서코드")
    @JsonProperty("orgCd")
    private String orgCd;

    @Schema(description = "팀코드")
    @JsonProperty("teamCd")
    private String teamCd;

    @Schema(description = "작성자ID")
    @JsonProperty("writerId")
    private String writerId;

    @Schema(description = "비고")
    @JsonProperty("docRemark")
    private String docRemark;

    @Schema(description = "문서상태")
    @JsonProperty("docStatus")
    private String docStatus;

    @Schema(description = "확정시간")
    @JsonProperty("confirmDt")
    private String confirmDt;

    @Schema(description = "승인상태")
    @JsonProperty("approvalStatus")
    private String approvalStatus;

    @Schema(description = "전자계약문서ID")
    @JsonProperty("eformDocId")
    private String eformDocId;

    @Schema(description = "참조문서번호")
    @JsonProperty("refDocNo")
    private String refDocNo;

}
