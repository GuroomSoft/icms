package com.guroomsoft.icms.biz.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.guroomsoft.icms.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.type.Alias;

@Schema(description = "HT_AGREEMENT - 가격변경합의문서")
@EqualsAndHashCode(callSuper = true)
@Data
@Alias("agreementDoc")
public class AgreementDoc extends BaseDTO {
    @Schema(description = "문서번호")  @JsonProperty("docNo") private String docNo;
    @Schema(description = "플랜트코드")  @JsonProperty("plantCd") private String plantCd;
    @Schema(description = "공시단가 등록일")  @JsonProperty("announcedDate") private String announcedDate;
    @Schema(description = "발송인 팀명")  @JsonProperty("senderTeam") private String senderTeam;
    @Schema(description = "발송인")  @JsonProperty("sender") private String sender;
    @Schema(description = "BP코드")  @JsonProperty("bpCd") private String bpCd;
    @Schema(description = "BP명")  @JsonProperty("bpNm") private String bpNm;
    @Schema(description = "대표자명")  @JsonProperty("ceoNm") private String ceoNm;
    @Schema(description = "특이사항")  @JsonProperty("agrDesc") private String  agrDesc;
    @Schema(description = "상세내용 이미지 BASE64 코드값")  @JsonProperty("agrDetail") private String  agrDetail;
    @Schema(description = "가격변경 문서번호")  @JsonProperty("srcDocNo") private String  srcDocNo;
    @Schema(description = "이폼사인 문서ID")  @JsonProperty("eformDocId") private String  eformDocId;
    @Schema(description = "이폼사인 문서명")  @JsonProperty("eformDocName") private String  eformDocName;
    @Schema(description = "이폼사인 현재상태코드")  @JsonProperty("eformStatus") private String  eformStatus;
    @Schema(description = "요청일시")  @JsonProperty("requestDt") private String requestDt;
    @Schema(description = "완료일시")  @JsonProperty("completeDt") private String completeDt;
    @Schema(description = "최근변경일시")  @JsonProperty("lastChgDt") private String lastChgDt;
}
