package com.guroomsoft.icms.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

import java.util.List;

/**
 * Table :
 *  CT_FILES
 */

@EqualsAndHashCode(callSuper = true)
@Data
@Alias("exchangeRate")
public class ExchangeRate extends BaseDTO {
    @Schema(description = "기준일")
    @JsonProperty("stdDate")
    private String stdDate;

    @Schema(description = "통화코드")
    @JsonProperty("curUnit")
    private String curUnit;

    @Schema(description = "국가/통화명")
    @JsonProperty("curNm")
    private String curNm;

    @Schema(description = "전신환(송금) 받으실때")
    @JsonProperty("ttb")
    private String ttb;

    @Schema(description = "전신환(송금) 보내실때")
    @JsonProperty("tts")
    private String tts;

    @Schema(description = "매매 기준율")
    @JsonProperty("dealBasR")
    private String dealBasR;

    @Schema(description = "장부가격")
    @JsonProperty("bkpr")
    private String bkpr;

    @Schema(description = "년환가료율")
    @JsonProperty("yyEfeeR")
    private String yyEfeeR;

    @Schema(description = "10일환가료율")
    @JsonProperty("tenDdEfeeR")
    private String tenDdEfeeR;

    @Schema(description = "서울외국환중개 매매기준율")
    @JsonProperty("kftcDealBasR")
    private String kftcDealBasR;

    @Schema(description = "서울외국환중개 장부가격")
    @JsonProperty("kftcBkpr")
    private String kftcBkpr;
}
