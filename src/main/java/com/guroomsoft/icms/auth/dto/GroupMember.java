package com.guroomsoft.icms.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("groupMember")
public class GroupMember extends User {
    @Schema(description = "사용자 그룹 ID")
    @JsonProperty("grpUid")
    private Long grpUid;

    @Schema(description = "사용자여부(Y/N)")
    @JsonProperty("useAt")
    private String useAt;


}
