package com.guroomsoft.icms.biz.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "MT_ITEM 요청")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("itemReq")
public class ItemReq extends Item {
    @JsonIgnore
    private String searchWord;

    @JsonIgnore
    private String materialCd;

    @JsonIgnore
    private String rawMaterialCd;

    @JsonIgnore
    private String customerMatCd;

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
