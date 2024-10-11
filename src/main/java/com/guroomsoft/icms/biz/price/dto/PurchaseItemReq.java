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
@Alias("purchaseItemReq")
public class PurchaseItemReq {
    @Schema(description = "공시단가문서번호") @JsonProperty("docNo") private String docNo;
    @Schema(description = "등록월(YYYYMMDD)") @JsonProperty("announcedDate") private String announcedDate;
    @Schema(description = "플랜트코드") @JsonProperty("plantCd") private String plantCd;
    @Schema(description = "원소재적용월(YYYYMMDD)") @JsonProperty("applyDate") private String applyDate;
    @Schema(description = "원소재적용월(YYYYMMDD)") @JsonProperty("applyDateList") private List<String> applyDateList;
    @Schema(description = "협력사코드목록") @JsonProperty("bpList") private List<String> bpList;
    @Schema(description = "협력사코드") @JsonProperty("bpCd") private String bpCd;
    @Schema(description = "매입품번") @JsonProperty("itemNo") private String itemNo;
}
