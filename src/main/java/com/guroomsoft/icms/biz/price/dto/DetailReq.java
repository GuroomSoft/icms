package com.guroomsoft.icms.biz.price.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * Table Name:  가격변경이력상세
 *  HT_CHANGED_PRICE
 */
@Data
@Alias("detailReq")
public class DetailReq {
    @Schema(description = "공시단가문서번호") @JsonProperty("docNo") private String docNo;
    @Schema(description = "플랜트코드") @JsonProperty("plantCd") private String plantCd;
    @Schema(description = "협력사코드목록") @JsonProperty("bpList") private List<String> bpList;
    @Schema(description = "차종") @JsonProperty("carModel") private String carModel;
    @Schema(description = "부품구분코드") @JsonProperty("partType") private String partType;
    @Schema(description = "원소재코드") @JsonProperty("rawMaterialCd") private String rawMaterialCd;
    @Schema(description = "매입품번") @JsonProperty("pcsItemNo") private String pcsItemNo;
    @Schema(description = "SUB품번") @JsonProperty("subItemNo") private String subItemNo;
    @Schema(description = "재질코드") @JsonProperty("materialCd") private String materialCd;
    @Schema(description = "개별가격변동내역상세상태") @JsonProperty("changedStatus") private String changedStatus;
}
