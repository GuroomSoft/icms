package com.guroomsoft.icms.biz.bbs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.AttachFileDtl;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Schema(description = "CT_BBS - 게시판")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("notice")
public class Notice extends BaseDTO {
    @Schema(description = "행번호")
    @JsonProperty("rnum")
    private Long rnum;

    @Schema(description = "게시판 ID")
    @JsonProperty("bbsId")
    private String bbsId;

    @Schema(description = "게시물 UID")
    @JsonProperty("nttUid")
    private Long nttUid;

    @Schema(description = "게시물 분류")
    @JsonProperty("nttCategory")
    private String nttCategory;

    @Schema(description = "게시물 분류명")
    @JsonProperty("nttCategoryNm")
    private String nttCategoryNm;


    @Schema(description = "게시물 제목")
    @JsonProperty("nttSubject")
    private String nttSubject;

    @Schema(description = "게시물")
    @JsonProperty("nttContent")
    private String nttContent;

    @Schema(description = "조회수")
    @JsonProperty("readCount")
    private Integer readCount;

    @Schema(description = "상단고정여부")
    @JsonProperty("topLockAt")
    private String topLockAt;

    @Schema(description = "답변가능여부")
    @JsonProperty("answerAt")
    private String answerAt;

    @Schema(description = "부모글 UID")
    @JsonProperty("parentNttUid")
    private Long parentNttUid;

    @Schema(description = "답글레벨")
    @JsonProperty("answerLvl")
    private Integer answerLvl;

    @Schema(description = "답글 비밀번호")
    @JsonProperty("nttPwd")
    private String nttPwd;

    @Schema(description = "정렬순서")
    @JsonProperty("sortOrder")
    private Integer sortOrder;

    @Schema(description = "작성일시")
    @JsonProperty("writeDt")
    private String writeDt;

    @Schema(description = "작성자ID")
    @JsonProperty("writerId")
    private String writerId;

    @Schema(description = "작성자명")
    @JsonProperty("writer")
    private String writer;

    @Schema(description = "게시일자")
    @JsonProperty("pubBeginDate")
    private String pubBeginDate;

    @Schema(description = "게시종료일자")
    @JsonProperty("pubEndDate")
    private String pubEndDate;

    @Schema(description = "첨부문서ID")
    @JsonProperty("atchFileId")
    private String atchFileId;

    @Schema(description = "사용여부(Y/N)")
    @JsonProperty("useAt")
    private String useAt;

    @Schema(description = "첨부파일 목록")
    @JsonProperty("attachFiles")
    private List<AttachFileDtl> attachFiles;
}
