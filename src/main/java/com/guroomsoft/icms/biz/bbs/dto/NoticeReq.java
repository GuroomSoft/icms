package com.guroomsoft.icms.biz.bbs.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "CT_BBS - 게시판 요청")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("noticeReq")
public class NoticeReq extends Notice {

    @JsonIgnore
    private String searchWord;

    @JsonIgnore
    private String fromDate;

    @JsonIgnore
    private String toDate;

    @JsonIgnore
    private int rowCountPerPage;

    @JsonIgnore
    private int pageNumber;

    @JsonIgnore
    private int totalCount;

    @JsonIgnore
    private int totalPageCount;

    @JsonIgnore
    private int startOffset;

}
