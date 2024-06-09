package com.guroomsoft.icms.biz.templateDoc.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("templateDoc")
public class TemplateDoc extends BaseDTO {
    @Schema(description = "문서번호")
    @JsonProperty("docNo")
    private String docNo;

    @Schema(description = "문서제목")
    @JsonProperty("docTitle")
    private String docTitle;

    @Schema(description = "문서파일명-경로제외")
    @JsonProperty("docFilename")
    private String docFilename;

    @Schema(description = "작성일시")
    @JsonProperty("writeDt")
    private String writeDt;

    @Schema(description = "작성자 UID")
    @JsonProperty("writerUid")
    private Long writerUid;

    @Schema(description = "작성자 ID")
    @JsonProperty("writerId")
    private String writerId;

    @Schema(description = "문서상태")
    @JsonProperty("docStatus")
    private String docStatus;

    @Schema(description = "문서상태명")
    @JsonProperty("docStatusNm")
    private String docStatusNm;

    @Schema(description = "문서암호")
    @JsonProperty("docPwd")
    private String docPwd;

    @Schema(description = "확정일시")
    @JsonProperty("confirmDt")
    private String confirmDt;

    @Schema(description = "비고")
    @JsonProperty("docRemark")
    private String docRemark;

    @Schema(description = "문서원본")
    @JsonProperty("docContent")
    private String docContent;

    @Schema(description = "등록자ID")
    @JsonProperty("regAccountId")
    private String regAccountId;

    @Schema(description = "수정자ID")
    @JsonProperty("modAccountId")
    private String modAccountId;

    @Schema(description = "상세내역")
    @JsonProperty("details")
    private List<TemplateDtl> details;

}
