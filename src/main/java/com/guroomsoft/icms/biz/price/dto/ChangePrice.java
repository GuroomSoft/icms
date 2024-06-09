package com.guroomsoft.icms.biz.price.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * Table Name:  가격변경이력상세
 *  HT_CHANGED_PRICE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("changePrice")
public class ChangePrice extends BaseDTO {
    @Schema(description = "문서번호") @JsonProperty("docNo")  private String docNo;
    @Schema(description = "기준일") @JsonProperty("announcedDate") private String announcedDate;
    @Schema(description = "플랜트코드") @JsonProperty("plantCd")  private String plantCd;
    @Schema(description = "플랜트명") @JsonProperty("plantNm") private String plantNm;
    @Schema(description = "협력사코드") @JsonProperty("bpCd") private String bpCd;
    @Schema(description = "협력사명") @JsonProperty("bpNm") private String bpNm;
    @Schema(description = "차수") @JsonProperty("docOrder") private String docOrder;
    @Schema(description = "차종") @JsonProperty("carModel") private String carModel;
    @Schema(description = "매입품번") @JsonProperty("pcsItemNo") private String pcsItemNo;
    @Schema(description = "매입품명") @JsonProperty("pcsItemNm") private String pcsItemNm;
    @Schema(description = "현재가일자") @JsonProperty("baseDate") private String baseDate;
    @Schema(description = "현재가") @JsonProperty("curItemPrice") private String curItemPrice;
    @Schema(description = "부품구분코드") @JsonProperty("partType") private String partType;
    @Schema(description = "부품구분명") @JsonProperty("partTypeNm") private String partTypeNm;
    @Schema(description = "사급품목 협력사코드") @JsonProperty("subItemBpCd") private String subItemBpCd;
    @Schema(description = "사급품목 협력사명") @JsonProperty("subItemBpNm") private String subItemBpNm;
    @Schema(description = "SUB 품번") @JsonProperty("subItemNo") private String subItemNo;
    @Schema(description = "SUB 품명") @JsonProperty("subItemNm") private String subItemNm;
    @Schema(description = "원소재코드") @JsonProperty("rawMaterialCd") private String rawMaterialCd;
    @Schema(description = "원소재명") @JsonProperty("rawMaterialNm") private String rawMaterialNm;
    @Schema(description = "기준일") @JsonProperty("applyDate") private String applyDate;
    @Schema(description = "재질코드") @JsonProperty("materialCd") private String materialCd;
    @Schema(description = "재질명") @JsonProperty("materialNm") private String materialNm;
    @Schema(description = "US") @JsonProperty("us") private Integer us;
    @Schema(description = "강종") @JsonProperty("steelGrade") private String steelGrade;
    @Schema(description = "M-SPEC") @JsonProperty("mSpec") private String mSpec;
    @Schema(description = "M-TYPE") @JsonProperty("mType") private String mType;
    @Schema(description = "두께/두께") @JsonProperty("thickThick") private String thickThick;
    @Schema(description = "가로/외경") @JsonProperty("widthOuter") private String widthOuter;
    @Schema(description = "세로/투입길이") @JsonProperty("heightInLen") private String heightInLen;
    @Schema(description = "BL-가로") @JsonProperty("blWidth") private String blWidth;
    @Schema(description = "BL-세로") @JsonProperty("blLength") private String blLength;
    @Schema(description = "BL-CAVITY") @JsonProperty("blCavity") private String blCavity;
    @Schema(description = "NET중량(Kg)") @JsonProperty("netWeight") private String netWeight;
    @Schema(description = "비중") @JsonProperty("specificGravity") private String specificGravity;
    @Schema(description = "SLITT LOSS 율(%)") @JsonProperty("slittLossRate") private String slittLossRate;
    @Schema(description = "LOSS 율(%)") @JsonProperty("lossRate") private String lossRate;
    @Schema(description = "투입중량(Kg)") @JsonProperty("inputWeight") private String inputWeight;
    @Schema(description = "사급단가(이전)") @JsonProperty("bfConsignedPrice") private String bfConsignedPrice;
    @Schema(description = "사급단가(이후)") @JsonProperty("afConsignedPrice") private String afConsignedPrice;
    @Schema(description = "사급단가 차액") @JsonProperty("diffConsignedPrice") private String diffConsignedPrice;
    @Schema(description = "사급재료비(이전/매)") @JsonProperty("bfCnsgnMatPrice") private String bfCnsgnMatPrice;
    @Schema(description = "사급재료비(이후/매)") @JsonProperty("afCnsgnMatPrice") private String afCnsgnMatPrice;
    @Schema(description = "사급재료비 차액") @JsonProperty("diffCnsgnMatPrice") private String diffCnsgnMatPrice;
    @Schema(description = "SCRAP 단가(이전/Kg)") @JsonProperty("bfScrapUnitPrice") private String bfScrapUnitPrice;
    @Schema(description = "SCRAP 단가(이후/Kg)") @JsonProperty("afScrapUnitPrice") private String afScrapUnitPrice;
    @Schema(description = "SCRAP 단가 차액") @JsonProperty("diffScrapUnitPrice") private String diffScrapUnitPrice;
    @Schema(description = "Scrap 중량(Kg/EA)") @JsonProperty("scrapWeight") private String scrapWeight;
    @Schema(description = "Scrap 회수율(%)") @JsonProperty("scrapRecoveryRate") private String scrapRecoveryRate;
    @Schema(description = "SCRAP 가격(이전/매)") @JsonProperty("bfScrapPrice") private String bfScrapPrice;
    @Schema(description = "SCRAP 가격(이후/매)") @JsonProperty("afScrapPrice") private String afScrapPrice;
    @Schema(description = "SCRAP 가격 차액") @JsonProperty("diffScrapPrice") private String diffScrapPrice;
    @Schema(description = "부품재료비(이전)") @JsonProperty("bfPartMatCost") private String bfPartMatCost;
    @Schema(description = "부품재료비(이후)") @JsonProperty("afPartMatCost") private String afPartMatCost;
    @Schema(description = "부품재료비차액") @JsonProperty("diffPartMatCost") private String diffPartMatCost;
    @Schema(description = "재관비율(%)") @JsonProperty("matAdminRate") private String matAdminRate;
    @Schema(description = "외주재관비율(%)") @JsonProperty("osMatAdminRate") private String osMatAdminRate;
    @Schema(description = "변동금액") @JsonProperty("changedAmount") private String changedAmount;
    @Schema(description = "변경상태") @JsonProperty("changedStatus") private String changedStatus;
    @Schema(description = "SUB품목수") @JsonProperty("subItemCount") private Integer subItemCount;
    @Schema(description = "가격변경완료수") @JsonProperty("completedCnt") private Integer completedCnt;
    @Schema(description = "미완료수") @JsonProperty("incompletedCnt") private Integer incompletedCnt;
    @Schema(description = "에러수") @JsonProperty("errorCnt") private Integer errorCnt;
    @Schema(description = "변경금액합계") @JsonProperty("totalChangedAmt") private String totalChangedAmt;
    @Schema(description = "새 적용금액") @JsonProperty("newPurchaseAmt") private String newPurchaseAmt;
}
