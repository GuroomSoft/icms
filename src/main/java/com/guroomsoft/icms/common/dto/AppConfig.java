package com.guroomsoft.icms.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("appConfig")
public class AppConfig {
    @Schema(description = "환경설정키")
    @JsonProperty("envKey")
    private String envKey;

    @Schema(description = "설정값")
    @JsonProperty("envValue")
    private String envValue;

    @Schema(description = "카테고리")
    @JsonProperty("category")
    private String category;

    @Schema(description = "비")
    @JsonProperty("envRemark")
    private String envRemark;

    @JsonIgnore
    private String regDt;

    @JsonIgnore
    private Long regUid;

}
