package com.guroomsoft.icms.biz.templateDoc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "BT_TEMPLATE_DTL - 템플릿 상세 정보")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("templateDtlReq")
public class TemplateDtlReq extends BaseDTO {
    @JsonIgnore    private String docNo;

    @JsonIgnore    private String plantCd;

    @JsonIgnore    private String bpCd;

    @JsonIgnore    private String carModel;

    @JsonIgnore    private String pcsItemNo;

    @JsonIgnore    private String partType;

    @JsonIgnore    private String pcsSubItemBp;

    @JsonIgnore    private String subItemNo;

    @JsonIgnore    private String rawMaterialCd;

    @JsonIgnore    private String materialCd;

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
