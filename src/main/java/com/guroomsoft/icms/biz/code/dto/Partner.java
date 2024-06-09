package com.guroomsoft.icms.biz.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "MT_BIZ_PARTNER - 협력사 정보")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("partner")
public class Partner extends BaseDTO {
    @Schema(description = "회사코드")
    @JsonProperty("corpCd")
    private String corpCd;

    @Schema(description = "회사명")
    @JsonProperty("corpNm")
    private String corpNm;

    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "플랜트명")
    @JsonProperty("plantNm")
    private String plantNm;

    @Schema(description = "BP코드")
    @JsonProperty("bpCd")
    private String bpCd;

    @Schema(description = "BP명")
    @JsonProperty("bpNm")
    private String bpNm;

    @Schema(description = "BP정식명")
    @JsonProperty("bpTaxNm")
    private String bpTaxNm;

    @Schema(description = "사업자등록번호")
    @JsonProperty("bizRegNo")
    private String bizRegNo;

    @Schema(description = "대표자")
    @JsonProperty("ceoNm")
    private String ceoNm;

    @Schema(description = "우편번호")
    @JsonProperty("postNo")
    private String postNo;

    @Schema(description = "주소")
    @JsonProperty("bpAdrs")
    private String bpAdrs;

    @Schema(description = "대표이메일")
    @JsonProperty("bpEmail")
    private String bpEmail;

    @Schema(description = "대표전화")
    @JsonProperty("bpTelNo")
    private String bpTelNo;

    @Schema(description = "특이사항")
    @JsonProperty("bpRemark")
    private String bpRemark;

    @Schema(description = "담당자명")
    @JsonProperty("contactNm")
    private String contactNm;

    @Schema(description = "담당자이메일")
    @JsonProperty("contactEmail")
    private String contactEmail;

    @Schema(description = "담당자모바일전화")
    @JsonProperty("contactMobile")
    private String contactMobile;

    @Schema(description = "사용여부(C02)")
    @JsonProperty("useAt")
    private String useAt;

    @Schema(description = "사용여부명")
    @JsonProperty("useAtNm")
    private String useAtNm;

    @Schema(description = "노출여부(C04)")
    @JsonProperty("displayAt")
    private String displayAt;

    @Schema(description = "노출여부명")
    @JsonProperty("displayAtNm")
    private String displayAtNm;

    @Schema(description = "표시순서")
    @JsonProperty("displayOrd")
    private Integer displayOrd;

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

    @Schema(description = "담당자 UID")
    @JsonProperty("userUid")
    private Long userUid;

}
