package com.guroomsoft.icms.biz.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("plant")
public class Plant extends BaseDTO {
    @Schema(description = "플랜트코드")
    @JsonProperty("plantCd")
    private String plantCd;

    @Schema(description = "플랜트명")
    @JsonProperty("plantNm")
    private String plantNm;

    @Schema(description = "회사코드")
    @JsonProperty("corpCd")
    private String corpCd;

    @Schema(description = "회사명")
    @JsonProperty("corpNm")
    private String corpNm;

    @Schema(description = "국가(C08)")
    @JsonProperty("plantCountry")
    private String plantCountry;

    @Schema(description = "국가")
    @JsonProperty("plantCountryNm")
    private String plantCountryNm;

    @Schema(description = "사용여부")
    @JsonProperty("useAt")
    private String useAt;

    @Schema(description = "사용여부명")
    @JsonProperty("useAtNm")
    private String useAtNm;

    @Schema(description = "순서")
    @JsonProperty("ord")
    private Integer ord;

}
