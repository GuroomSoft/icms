package com.guroomsoft.icms.biz.price.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * Table Name:  공급업체 사급단가(매입단가)
 *  HT_PURCHASE_PRICE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("purchasePrice")
public class PurchasePrice extends BaseDTO {
    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "플랜트명")
    @JsonProperty("plantNm")
    private String plantNm;

    @Schema(description = "구매조직(1000 내수, 1100 외자)")
    @JsonProperty("purOrg")
    private String purOrg;

    @Schema(description = "공급업체 코드")
    @JsonProperty("bpCd")
    private String bpCd;

    @Schema(description = "공급업체명")
    @JsonProperty("bpNm")
    private String bpNm;

    @Schema(description = "자재코드")
    @JsonProperty("matCd")
    private String matCd;

    @Schema(description = "자재명")
    @JsonProperty("matNm")
    private String matNm;

    @Schema(description = "통화키(KRW)")
    @JsonProperty("curUnit")
    private String curUnit;

    @Schema(description = "구매단가-가격단위적용단가")
    @JsonProperty("purPrice")
    private BigDecimal purPrice;

    @Schema(description = "구매단가-가격단위 1 적용단가")
    @JsonProperty("originPurPrice")
    private BigDecimal originPurPrice;

    @Schema(description = "가격결정단위")
    @JsonProperty("priceUnit")
    private Integer priceUnit;

    @Schema(description = "구매단위")
    @JsonProperty("purchaseUnit")
    private String purchaseUnit;

    @Schema(description = "단가상태 10 정단가, 20 가단가")
    @JsonProperty("priceStatus")
    private String priceStatus;

    @Schema(description = "효력시작일")
    @JsonProperty("bgnValidDate")
    private String bgnValidDate;

    @Schema(description = "I/F 번호")
    @JsonProperty("ifSeq")
    private String ifSeq;

    @Schema(description = "I/F 유형")
    @JsonProperty("ifType")
    private String ifType;

    @Schema(description = "I/F 결과")
    @JsonProperty("ifResult")
    private String ifResult;

    @Schema(description = "I/F 메시지")
    @JsonProperty("ifMessage")
    private String ifMessage;

    @Schema(description = "등록자 ID")
    @JsonProperty("regAccountId")
    private String regAccountId;

}
