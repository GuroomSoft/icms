package com.guroomsoft.icms.biz.marketprice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;

@Schema(description = "HT_MARKET_PRICE_NONFERROUS - 비철금속 NME 시세 ")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("nonferrousPrice")
public class NonferrousPrice extends BaseDTO {
    @Schema(description = "기준일")
    @JsonProperty("mnDate")
    private String mnDate;

    @Schema(description = "구리 가격")
    @JsonProperty("cuPrc")
    private BigDecimal cuPrc;

    @Schema(description = "알루미늄 가격")
    @JsonProperty("alPrc")
    private BigDecimal alPrc;

    @Schema(description = "Zn 가격")
    @JsonProperty("znPrc")
    private BigDecimal znPrc;

    @Schema(description = "Pb 가격")
    @JsonProperty("pbPrc")
    private BigDecimal pbPrc;

    @Schema(description = "Ni 가격")
    @JsonProperty("niPrc")
    private BigDecimal niPrc;

    @Schema(description = "sn 가격")
    @JsonProperty("snPrc")
    private BigDecimal snPrc;
}
