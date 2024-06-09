package com.guroomsoft.icms.biz.templateDoc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

@Schema(description = "BT_TEMPLATE_DTL - 템플릿 상세 정보")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("templateDtl")
public class TemplateDtl extends BaseDTO {
    @Schema(description = "문서번호")
    @JsonProperty("docNo")
    private String docNo;

    @Schema(description = "항목순번")
    @JsonProperty("dtSeq")
    private Long tdSeq;

    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "플랜트")
    @JsonProperty("plantNm")
    private String plantNm;

    @Schema(description = "BP코드")
    @JsonProperty("bpCd")
    private String bpCd;

    @Schema(description = "협력사명")
    @JsonProperty("bpNm")
    private String bpNm;

    @Schema(description = "차수")
    @JsonProperty("docOrder")
    private String docOrder;

    @Schema(description = "차종")
    @JsonProperty("carModel")
    private String carModel;

    @Schema(description = "매입품번")
    @JsonProperty("pcsItemNo")
    private String pcsItemNo;

    @Schema(description = "매입품목 품명")
    @JsonProperty("pcsItemNm")
    private String pcsItemNm;

    @Schema(description = "현재가일자")
    @JsonProperty("baseDate")
    private String baseDate;

    @Schema(description = "현재가")
    @JsonProperty("curItemPrice")
    private BigDecimal curItemPrice;

    @Schema(description = "부품구분(B02)")
    @JsonProperty("partType")
    private String partType;

    @Schema(description = "부품구분명")
    @JsonProperty("partTypeNm")
    private String partTypeNm;

    @Schema(description = "Sub 부품 매입사 코드")
    @JsonProperty("pcsSubItemBp")
    private String pcsSubItemBp;

    @Schema(description = "Sub 부품 매입사 코드")
    @JsonProperty("pcsSubItemBpNm")
    private String pcsSubItemBpNm;

    @Schema(description = "Sub 품번")
    @JsonProperty("subItemNo")
    private String subItemNo;

    @Schema(description = "Sub 품명")
    @JsonProperty("subItemNm")
    private String subItemNm;

    @Schema(description = "원소재코드")
    @JsonProperty("rawMaterialCd")
    private String rawMaterialCd;

    @Schema(description = "원소재명")
    @JsonProperty("rawMaterialNm")
    private String rawMaterialNm;

    @Schema(description = "재질코드")
    @JsonProperty("materialCd")
    private String materialCd;

    @Schema(description = "재질명")
    @JsonProperty("materialNm")
    private String materialNm;

    @Schema(description = "US")
    @JsonProperty("us")
    private Integer us;

    @Schema(description = "강종")
    @JsonProperty("steelGrade")
    private String steelGrade;

    @Schema(description = "M-Spec")
    @JsonProperty("mSpec")
    private String mSpec;

    @Schema(description = "M-Type")
    @JsonProperty("mType")
    private String mType;

    @Schema(description = "두께/두께")
    @JsonProperty("thickThick")
    private BigDecimal thickThick;

    @Schema(description = "가로/외경")
    @JsonProperty("widthOuter")
    private BigDecimal widthOuter;

    @Schema(description = "세로/투입길이")
    @JsonProperty("heightInLen")
    private BigDecimal heightInLen;

    @Schema(description = "BL-가로")
    @JsonProperty("blWidth")
    private BigDecimal blWidth;

    @Schema(description = "BL-세로")
    @JsonProperty("blLength")
    private BigDecimal blLength;

    @Schema(description = "BL-CAVITY")
    @JsonProperty("blCavity")
    private BigDecimal blCavity;

    @Schema(description = "NET중량(Kg)")
    @JsonProperty("netWeight")
    private BigDecimal netWeight;

    @Schema(description = "비중")
    @JsonProperty("specificGravity")
    private BigDecimal specificGravity;

    @Schema(description = "SLITT LOSS 율(%)")
    @JsonProperty("slittLossRate")
    private BigDecimal slittLossRate;

    @Schema(description = "TO LOSS 율(%)")
    @JsonProperty("toLossRate")
    private BigDecimal toLossRate;

    @Schema(description = "투입중량(Kg)")
    @JsonProperty("inputWeight")
    private BigDecimal inputWeight;

    @Schema(description = "Scrap 중량(Kg/EA)")
    @JsonProperty("scrapWeight")
    private BigDecimal scrapWeight;

    @Schema(description = "Scrap 회수율(%)")
    @JsonProperty("scrapRecoveryRate")
    private BigDecimal scrapRecoveryRate;

    @Schema(description = "재관비율(%)")
    @JsonProperty("matAdminRate")
    private BigDecimal matAdminRate;

    @Schema(description = "외주재관비율(%)")
    @JsonProperty("osMatAdminRate")
    private BigDecimal osMatAdminRate;

    @Schema(description = "사급단가(이전/Kg)")
    @JsonProperty("bfConsignedPrice")
    private BigDecimal bfConsignedPrice;

    @Schema(description = "사급단가(이후/Kg)")
    @JsonProperty("afConsignedPrice")
    private BigDecimal afConsignedPrice;

    @Schema(description = "사급단가 차액")
    @JsonProperty("diffConsignedPrice")
    private BigDecimal diffConsignedPrice;

    @Schema(description = "사급재료비(이전/매)")
    @JsonProperty("bfCnsgnMatPrice")
    private BigDecimal bfCnsgnMatPrice;

    @Schema(description = "사급재료비(이후/매)")
    @JsonProperty("afCnsgnMatPrice")
    private BigDecimal afCnsgnMatPrice;

    @Schema(description = "사급재료비 차액")
    @JsonProperty("diffCnsgnMatPrice")
    private BigDecimal diffCnsgnMatPrice;

    @Schema(description = "SCRAP 단가(이전/Kg)")
    @JsonProperty("bfScrapUnitPrice")
    private BigDecimal bfScrapUnitPrice;

    @Schema(description = "SCRAP 단가(이후/Kg)")
    @JsonProperty("afScrapUnitPrice")
    private BigDecimal afScrapUnitPrice;

    @Schema(description = "SCRAP 단가 차액")
    @JsonProperty("diffScrapUnitPrice")
    private BigDecimal diffScrapUnitPrice;

    @Schema(description = "SCRAP 가격(이전/매)")
    @JsonProperty("bfScrapPrice")
    private BigDecimal bfScrapPrice;

    @Schema(description = "SCRAP 가격(이후/매)")
    @JsonProperty("afScrapPrice")
    private BigDecimal afScrapPrice;

    @Schema(description = "SCRAP 가격 차액")
    @JsonProperty("diffScrapPrice")
    private BigDecimal diffScrapPrice;

    @Schema(description = "부품재료비(이전)")
    @JsonProperty("bfPartMatCost")
    private BigDecimal bfPartMatCost;

    @Schema(description = "부품재료비(이후)")
    @JsonProperty("afPartMatCost")
    private BigDecimal afPartMatCost;

    @Schema(description = "부품재료비차액")
    @JsonProperty("diffPartMatCost")
    private BigDecimal diffPartMatCost;

    @Schema(description = "변동금액")
    @JsonProperty("changedAmount")
    private BigDecimal changedAmount;

    @Schema(description = "담당자ID")
    @JsonProperty("writerId")
    private String writerId;

    @Schema(description = "유효성검사 메시지")
    @JsonProperty("invalidMessage")
    private String invalidMessage;

    @Schema(description = "등록자ID")
    @JsonProperty("regAccountId")
    private String regAccountId;

    @Schema(description = "수정자ID")
    @JsonProperty("modAccountId")
    private String modAccountId;

    @Schema(description = "등록자명")
    @JsonProperty("regAccountNm")
    private String regAccountNm;

    @Schema(description = "수정자명")
    @JsonProperty("modAccountNm")
    private String modAccountNm;

}
