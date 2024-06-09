package com.guroomsoft.icms.biz.price.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.guroomsoft.icms.common.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * Table Name:  매출업체 사급단가
 *  HT_CONSIGNED_PRICE
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("consignedPriceReq")
public class ConsignedPriceReq extends BaseDTO {
    @JsonIgnore
    private String plantCd;

    @JsonIgnore
    private String baseDate;

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
