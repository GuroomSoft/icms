package com.guroomsoft.icms.biz.material.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("announcePriceDoc")
public class AnnouncePrice extends BaseDTO {
    @Schema(description = "문서번호")
    @JsonProperty("docNo")
    private String docNo;

    @Schema(description = "문서명")
    @JsonProperty("docTitle")
    private String docTitle;

    @Schema(description = "국가코드")
    @JsonProperty("countryCd")
    private String countryCd;

    @Schema(description = "국가명")
    @JsonProperty("countryNm")
    private String countryNm;

    @Schema(description = "작성자 UID")
    @JsonProperty("writerUid")
    private Long writerUid;

    @Schema(description = "작성자 계정 ID")
    @JsonProperty("writerId")
    private String writerId;

    @Schema(description = "작성자명")
    @JsonProperty("writerNm")
    private String writerNm;

    @Schema(description = "작성일시")
    @JsonProperty("writerDt")
    private String writerDt;

    @Schema(description = "기준일(고시일)")
    @JsonProperty("announcedDate")
    private String announcedDate;

    @Schema(description = "문서상태")
    @JsonProperty("docStatus")
    private String docStatus;

    @Schema(description = "문서상태명")
    @JsonProperty("docStatusNm")
    private String docStatusNm;

    @Schema(description = "문서 확정일시")
    @JsonProperty("confirmDt")
    private String confirmDt;

    @Schema(description = "비고")
    @JsonProperty("docRemark")
    private String docRemark;

    @Schema(description = "JSON 형식 엑셀파일 Content")
    @JsonProperty("docContent")
    private String docContent;

    @Schema(description = "등록자ID")
    @JsonProperty("regAccountId")
    private String regAccountId;

    @Schema(description = "수정자ID")
    @JsonProperty("modAccountId")
    private String modAccountId;

    @Schema(description = "재질별 공시단가 리스트")
    @JsonProperty("details")
    private List<AnnouncePriceDetail> details;
}
