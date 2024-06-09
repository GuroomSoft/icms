package com.guroomsoft.icms.biz.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "ST_CODE - 공통코드정보")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("code")
public class Code extends BaseDTO {
    @Schema(description = "코드그룹ID")
    @JsonProperty("cgId")
    private String cgId;

    @Schema(description = "코드")
    @JsonProperty("cd")
    private String cd;

    @Schema(description = "코드명칭")
    @JsonProperty("cdNm")
    private String cdNm;

    @Schema(description = "상위코드")
    @JsonProperty("upperCd")
    private String upperCd;

    @Schema(description = "비고")
    @JsonProperty("cdRemark")
    private String cdRemark;

    @Schema(description = "추가정보 1")
    @JsonProperty("addData1")
    private String addData1;

    @Schema(description = "추가정보 2")
    @JsonProperty("addData2")
    private String addData2;

    @Schema(description = "추가정보 3")
    @JsonProperty("addData3")
    private String addData3;

    @Schema(description = "디폴트값 여부 Y/N")
    @JsonProperty("defaultAt")
    private String defaultAt;

    @Schema(description = "노출여부(Y/N)")
    @JsonProperty("displayAt")
    private String displayAt;

    @Schema(description = "표시순서")
    @JsonProperty("displayOrd")
    private Integer displayOrd;

    @Schema(description = "사용여부(Y/N)")
    @JsonProperty("useAt")
    private String useAt;
}
