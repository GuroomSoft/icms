package com.guroomsoft.icms.biz.material.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("material")
public class Material extends BaseDTO {
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

    @Schema(description = "유형(강종)")
    @JsonProperty("steelGrade")
    private String steelGrade;

    @Schema(description = "고객사재질코드")
    @JsonProperty("customerMatCd")
    private String customerMatCd;

    @Schema(description = "비고")
    @JsonProperty("remark")
    private String remark;

    @Schema(description = "사용여부")
    @JsonProperty("useAt")
    private String useAt;

    @Schema(description = "사용여부(C03)")
    @JsonProperty("useAtNm")
    private String useAtNm;

    @Schema(description = "등록자")
    @JsonProperty("accountNm")
    private String accountNm;
}
