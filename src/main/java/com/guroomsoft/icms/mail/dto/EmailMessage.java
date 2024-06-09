package com.guroomsoft.icms.mail.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Builder
@Alias("EmailMessage")
public class EmailMessage {
    @Schema(description = "발송자")
    @JsonProperty("sender")
    private String sender;

    @Schema(description = "수신자")
    @JsonProperty("receiver")
    private String receiver;

    @Schema(description = "제목")
    @JsonProperty("title")
    private String title;

    @Schema(description = "내용")
    @JsonProperty("content")
    private String content;
}
