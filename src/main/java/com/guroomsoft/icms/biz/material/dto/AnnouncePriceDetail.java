package com.guroomsoft.icms.biz.material.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.util.Comparator;

/**
 * BT_ANNOUNCE_PRICE_DTL    공시단가 상세
 * BT_ANNOUNCE_PRICE_LAST   최종 공시단가
 * BT_ANNOUNCE_PRICE_BF     직전 공시단가
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("announcePriceDetail")
public class AnnouncePriceDetail extends BaseDTO implements Comparator<AnnouncePriceDetail> {
    @Schema(description = "문서번호")
    @JsonProperty("docNo")
    private String docNo;

    @Schema(description = "항목순번")
    @JsonProperty("apSeq")
    private Integer apSeq;

    @Schema(description = "원소재코드")
    @JsonProperty("rawMaterialCd")
    private String rawMaterialCd;

    @Schema(description = "원소재명")
    @JsonProperty("rawMaterialNm")
    private String rawMaterialNm;

    @Schema(description = "유형(강종)")
    @JsonProperty("steelGrade")
    private String steelGrade;

    @Schema(description = "재질코드")
    @JsonProperty("materialCd")
    private String materialCd;

    @Schema(description = "재질명")
    @JsonProperty("materialNm")
    private String materialNm;

    @Schema(description = "고객사 재질코드")
    @JsonProperty("customerMatCd")
    private String customerMatCd;


    @Schema(description = "시작일")
    @JsonProperty("bgnDate")
    private String bgnDate;

    @Schema(description = "종료일")
    @JsonProperty("endDate")
    private String endDate;

    @Schema(description = "변경전 재질단가")
    @JsonProperty("bfMatUnitPrice")
    private BigDecimal bfMatUnitPrice;

    @Schema(description = "재질단가차액")
    @JsonProperty("diffMatPrice")
    private BigDecimal diffMatPrice;

    @Schema(description = "재질단가")
    @JsonProperty("matUnitPrice")
    private BigDecimal matUnitPrice;

    @Schema(description = "변경전 SCRAP 단가")
    @JsonProperty("bfScrapPrice")
    private BigDecimal bfScrapPrice;

    @Schema(description = "SCRAP 단가")
    @JsonProperty("scrapPrice")
    private BigDecimal scrapPrice;

    @Schema(description = "SCRAP 단가 차액")
    @JsonProperty("diffScrapPrice")
    private BigDecimal diffScrapPrice;

    @Schema(description = "통화단위")
    @JsonProperty("currencyUnit")
    private String currencyUnit;

    @Schema(description = "국가코드")
    @JsonProperty("countryCd")
    private String countryCd;

    @Schema(description = "국가명")
    @JsonProperty("countryNm")
    private String countryNm;

    @Schema(description = "비고")
    @JsonProperty("mpRemark")
    private String mpRemark;

    @Schema(description = "등록자명")
    @JsonProperty("regUserNm")
    private String regUserNm;

    @Schema(description = "수정자명")
    @JsonProperty("modUserNm")
    private String modUserNm;

    @Schema(description = "등록 계정 ID")
    @JsonProperty("regAccountId")
    private String regAccountId;

    @Schema(description = "수정자 계정 ID")
    @JsonProperty("modAccountId")
    private String modAccountId;

    @Override
    public int compare(AnnouncePriceDetail o1, AnnouncePriceDetail o2) {
        return o1.bgnDate.compareTo(o2.getBgnDate());
    }
}
