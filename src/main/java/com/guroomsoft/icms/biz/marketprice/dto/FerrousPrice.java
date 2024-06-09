package com.guroomsoft.icms.biz.marketprice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "HT_MARKET_PRICE_FERROUS - 철강 시세 ")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("ferrousPrice")
public class FerrousPrice extends BaseDTO {
    @Schema(description = "기준일")
    @JsonProperty("mnDate")
    private String mnDate;

    @Schema(description = "품목")
    @JsonProperty("item")
    private String item;

    @Schema(description = "단위")
    @JsonProperty("unit")
    private String unit;

    @Schema(description = "거래시장(조건)")
    @JsonProperty("market")
    private String market;

    @Schema(description = "현물/선물")
    @JsonProperty("futures")
    private String futures;

    @Schema(description = "가격")
    @JsonProperty("price")
    private String price;

    @Schema(description = "전주평균")
    @JsonProperty("prevWeekAvg")
    private String prevWeekAvg;

    @Schema(description = "전월평균")
    @JsonProperty("prevMonthAvg")
    private String prevMonthAvg;

    @Schema(description = "전일대비 차액")
    @JsonProperty("prevDateAmt")
    private String prevDateAmt;

    @Schema(description = "전일비")
    @JsonProperty("prevDateRate")
    private String prevDateRate;

    @Schema(description = "전주대비 차액")
    @JsonProperty("prevWeekAmt")
    private String prevWeekAmt;

    @Schema(description = "전주비")
    @JsonProperty("prevWeekRate")
    private String prevWeekRate;

    @Schema(description = "전월대비 차액")
    @JsonProperty("prevMonthAmt")
    private String prevMonthAmt;

    @Schema(description = "전월비")
    @JsonProperty("prevMonthRate")
    private String prevMonthRate;

}
