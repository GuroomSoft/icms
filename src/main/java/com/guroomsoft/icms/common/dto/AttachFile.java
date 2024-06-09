package com.guroomsoft.icms.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * Table :
 *  CT_FILES
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("attachFile")
public class AttachFile extends BaseDTO {
    @Schema(description = "File UID")
    @JsonProperty("atchFileId")
    private String atchFileId;

    @Schema(description = "사용여부(Y/N)")
    @JsonProperty("useAt")
    private String useAt;

    @Schema(description = "첨부파일 상세")
    @JsonProperty("files")
    private List<AttachFileDtl> files;
}
