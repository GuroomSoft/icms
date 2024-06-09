package com.guroomsoft.icms.biz.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "MT_ITEM - 품번 정보")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("item")
public class Item extends BaseDTO {
    @Schema(description = "품번")
    @JsonProperty("itemNo")
    private String itemNo;

    @Schema(description = "품명")
    @JsonProperty("itemNm")
    private String itemNm;

    @Schema(description = "재질코드")
    @JsonProperty("materialCd")
    private String materialCd;

    @Schema(description = "재질명")
    @JsonProperty("materialNm")
    private String materialNm;

    @Schema(description = "원소재코드")
    @JsonProperty("rawMaterialCd")
    private String rawMaterialCd;

    @Schema(description = "원소재명")
    @JsonProperty("rawMaterialNm")
    private String rawMaterialNm;

    @Schema(description = "강종")
    @JsonProperty("steelGrade")
    private String steelGrade;

    @Schema(description = "고객사 재질코드")
    @JsonProperty("customerMatCd")
    private String customerMatCd;

    @Schema(description = "등록자ID")
    @JsonProperty("regAccountId")
    private String regAccountId;

    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "자재내역")
    @JsonProperty("spec")
    private String spec;

    @Schema(description = "차종")
    @JsonProperty("carModel")
    private String carModel;

    @Schema(description = "단위")
    @JsonProperty("unit")
    private String unit;

    @Schema(description = "사용여부")
    @JsonProperty("useAt")
    private String useAt;

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
}
