package com.guroomsoft.icms.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("group")
public class Group extends BaseDTO {
    @Schema(description = "사용자 그룹 ID")
    @JsonProperty("grpUid")
    private Long grpUid;

    @Schema(description = "사용자 그룹명")
    @JsonProperty("grpName")
    private String grpName;

    @Schema(description = "비고")
    @JsonProperty("grpRemark")
    private String grpRemark;

    @Schema(description = "사용여부")
    @JsonProperty("useAt")
    private String useAt;

}
