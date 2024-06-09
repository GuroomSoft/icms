package com.guroomsoft.icms.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.Menu;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * Table :
 *  ST_GROUP_AUTHORITY
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("groupAuthority")
public class GroupAuthority extends Menu {
    @Schema(description = "그룹 UID")
    @JsonProperty("grpUid")
    private Long grpUid;

    @Schema(description = "접근권한")
    @JsonProperty("viewPermAt")
    private String viewPermAt;

    @Schema(description = "등록권한")
    @JsonProperty("regPermAt")
    private String regPermAt;

    @Schema(description = "수정권한")
    @JsonProperty("modPermAt")
    private String modPermAt;

    @Schema(description = "삭제권한")
    @JsonProperty("delPermAt")
    private String delPermAt;

    @Schema(description = "실행권한")
    @JsonProperty("execPermAt")
    private String execPermAt;

    @Schema(description = "Sub Menu")
    @JsonProperty("_children")
    private List<GroupAuthority> child;

}
