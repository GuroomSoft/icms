package com.guroomsoft.icms.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("userRole")
public class UserRole extends BaseDTO {
    @Schema(description = "사용자 UID")
    @JsonProperty("userUid")
    private Long userUid;

    @Schema(description = "사용자 권한")
    @JsonProperty("userRole")
    private String userRole;
}
