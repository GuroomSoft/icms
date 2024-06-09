package com.guroomsoft.icms.biz.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "ST_CODE_GROUP - 공통코드분류정보")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("codeGroup")
public class CodeGroup extends BaseDTO {
    @Schema(description = "코드분류ID")
    @JsonProperty("cgId")
    private String cgId;

    @Schema(description = "코드분류명")
    @JsonProperty("cgNm")
    private String cgNm;

    @Schema(description = "비고")
    @JsonProperty("cgRemark")
    private String cgRemark;

    @Schema(description = "노출여부(Y/N)")
    @JsonProperty("displayAt")
    private String displayAt;

    @Schema(description = "사용여부(Y/N)")
    @JsonProperty("useAt")
    private String useAt;
}
