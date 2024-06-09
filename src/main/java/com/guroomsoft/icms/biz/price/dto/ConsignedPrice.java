package com.guroomsoft.icms.biz.price.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

/**
 * Table Name:  매출업체 사급단가
 *  HT_CONSIGNED_PRICE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("consignedPrice")
public class ConsignedPrice extends BaseDTO {
    @Schema(description = "SEQ")
    @JsonProperty("cpSeq")
    private Long cpSeq;

    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "플랜트명")
    @JsonProperty("plantNm")
    private String plantNm;

    @Schema(description = "판매처 BP 코드")
    @JsonProperty("bpCd")
    private String bpCd;

    @Schema(description = "판매처명")
    @JsonProperty("bpNm")
    private String bpNm;

    @Schema(description = "자재코드")
    @JsonProperty("matCd")
    private String matCd;

    @Schema(description = "자재명")
    @JsonProperty("matNm")
    private String matNm;

    @Schema(description = "통화키(KRW, USD)")
    @JsonProperty("curUnit")
    private String curUnit;

    @Schema(description = "출고단가 - 가격단위 적용단가")
    @JsonProperty("consignedPrice")
    private BigDecimal consignedPrice;

    @Schema(description = "출고단가 - 가격단위 1 적용단가")
    @JsonProperty("originConsignedPrice")
    private BigDecimal originConsignedPrice;

    @Schema(description = "가격결정단위")
    @JsonProperty("priceUnit")
    private Integer priceUnit;

    @Schema(description = "출고단위")
    @JsonProperty("salesUnit")
    private String salesUnit;

    @Schema(description = "효력 시작일")
    @JsonProperty("bgnValidDate")
    private String bgnValidDate;

    @Schema(description = "효력 종료일")
    @JsonProperty("endValidDate")
    private String endValidDate;

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
