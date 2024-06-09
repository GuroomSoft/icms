package com.guroomsoft.icms.biz.price.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.guroomsoft.icms.common.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * Table Name:  공급업체 사급단가 요청
 *  HT_PURCHASE_PRICE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("purchasePriceReq")
public class PurchasePriceReq extends BaseDTO {
    @JsonIgnore
    private String plantCd;

    @JsonIgnore
    private String purOrg;

    @JsonIgnore
    private String bpCd;

    @JsonIgnore
    private String searchWord;

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
