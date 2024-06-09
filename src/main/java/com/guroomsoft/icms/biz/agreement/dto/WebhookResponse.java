package com.guroomsoft.icms.biz.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Schema(description = "이폼사인 Webhook 응답")
@Data
@Alias("webhookResponse")
public class WebhookResponse {
    /*
    (
        webhookId=254da739744440b58a26f323ebfc6a0e,
        webhookName=webhook_cms,
        companyId=bc6eb696e0d144959f391df18faa9ea8,
        eventType=document,  document_action
        document=EforsignDoc(
            webhookId=test_doc_id,
            webhookName=test_document_title,
            companyId=test_template_id,
            eventType=test_template_name,
            workflowSeq=0,
            templateVersion=1,
            historyId=test_document_history_id,
            status=doc_create,
            editorId=jiyb0119@gmail.com,
            updatedDate=1713864126723
            )
      )
     */
    @Schema(description = "WebHook ID")  @JsonProperty("webhook_id") private String webhookId;
    @Schema(description = "WebHook 명")  @JsonProperty("webhook_name") private String webhookName;
    @Schema(description = "회사ID")  @JsonProperty("company_id") private String companyId;
    // 발생한 Webhook 이벤트의 종류 = document: 문서, document_action: ready_document_pdf: PDF
    @Schema(description = "이벤트유형")  @JsonProperty("event_type") private String eventType;
    @Schema(description = "분서정보")  @JsonProperty("document") private EforsignDoc document;
}
