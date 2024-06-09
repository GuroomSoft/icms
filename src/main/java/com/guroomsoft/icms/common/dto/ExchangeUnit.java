package com.guroomsoft.icms.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

/**
 * Table :
 *  MT_EXCHANGE_UNIT
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("exchangeUnit")
public class ExchangeUnit extends BaseDTO {
    @Schema(description = "통화코드")
    @JsonProperty("exchgUnit")
    private String exchgUnit;

    @Schema(description = "통화명")
    @JsonProperty("exchgNm")
    private String exchgNm;
}
