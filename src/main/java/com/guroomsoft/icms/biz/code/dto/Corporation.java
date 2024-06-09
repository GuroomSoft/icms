package com.guroomsoft.icms.biz.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Schema(description = "MT_CORPORATION - 회사 정보")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("corporation")
public class Corporation extends BaseDTO {
    @Schema(description = "회사코드")
    @JsonProperty("corpCd")
    private String corpCd;

    @Schema(description = "회사명")
    @JsonProperty("corpNm")
    private String corpNm;

    @Schema(description = "약식명")
    @JsonProperty("corpShortNm")
    private String corpShortNm;

    @Schema(description = "국가코드")
    @JsonProperty("corpCountry")
    private String corpCountry;

    @Schema(description = "국가명")
    @JsonProperty("corpCountryNm")
    private String corpCountryNm;

    @Schema(description = "사용여부")
    @JsonProperty("useAt")
    private String useAt;

    @Schema(description = "사용여부명")
    @JsonProperty("useAtNm")
    private String useAtNm;

    @Schema(description = "플랜트 목록")
    @JsonProperty("plants")
    private List<Plant> plants;

}
