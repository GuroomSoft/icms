package com.guroomsoft.icms.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * Table :
 *  CT_FILES
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("attachFileDtl")
public class AttachFileDtl extends BaseDTO {
    @Schema(description = "File UID")
    @JsonProperty("atchFileId")
    private String atchFileId;

    @Schema(description = "첨부파일 순번")
    @JsonProperty("fileSn")
    private Integer fileSn;

    @Schema(description = "파일 저장경로")
    @JsonProperty("fileStorePath")
    private String fileStorePath;

    @Schema(description = "접근경로")
    @JsonProperty("relativePath")
    private String relativePath;

    @Schema(description = "저장된 첨부파일 명")
    @JsonProperty("storeFileNm")
    private String storeFileNm;

    @Schema(description = "오리지널 첨부파일 명")
    @JsonProperty("originalFileNm")
    private String originalFileNm;

    @Schema(description = "파일확장자")
    @JsonProperty("fileExtension")
    private String fileExtension;

    @Schema(description = "파일크기")
    @JsonProperty("fileSize")
    private Long fileSize;

    @Schema(description = "사용여부(Y/N)")
    @JsonProperty("useAt")
    private String useAt;
}
