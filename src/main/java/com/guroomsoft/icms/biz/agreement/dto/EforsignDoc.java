package com.guroomsoft.icms.biz.agreement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.ibatis.type.Alias;

@Schema(description = "이폼사인 문서")
@Data
@Alias("eformsignDoc")
public class EforsignDoc {
    /*
        (
            webhookId=254da739744440b58a26f323ebfc6a0e,
            webhookName=webhook_cms,
            companyId=bc6eb696e0d144959f391df18faa9ea8,
            eventType=document,
            document=EforsignDoc(
                        docId=655fee73784f45f7bcc8b5ecdf846578,
                        documentTitle=가격합의서_1100_200022_20240401,
                        templateId=248dae82f2334222b718fb23358d6ef7,
                        templateName=가격합의서,
                        workflowSeq=1,
                        templateVersion=19,
                        historyId=0fb9e2f5467c404bbf39d61553149517,
                        status=doc_reject_participant,
                        editorId=jyb@papsnet.net,
                        updatedDate=1713869299909
            )
        )
     */
    /*
        문서상태코드
        doc_tempsave                초안(최초 작성자 문서 임시 저장 상태)
        doc_create                  문서 작성
        1. doc_request_participant     참여자 요청
        3. doc_accept_participant      참여자 승인
        doc_reject_participant      참여자 반려
        doc_request_reviewer        검토자 요청
        doc_accept_reviewer         검토자 승인
        doc_reject_reviewer         검토자 반려
        doc_reject_request          반려 요청
        doc_decline_cancel_request  반려 요청 거절
        doc_delete_request          삭제 요청
        doc_decline_delete_request  삭제 요청 거절
        doc_cancel_request          요청 취소
        doc_deleted                 문서 삭제
        doc_request_approval        결재 요청(구형 워크플로우)
        doc_accept_approval         결재 승인(구형 워크플로우)
        doc_reject_approval         결재 반려(구형 워크플로우)
        doc_request_external        외부자 요청(구형 워크플로우)
        doc_remind_external         외부자 재 요청(구형 워크플로우)
        doc_open_external           외부자 열람(구형 워크플로우)
        doc_accept_external         외부자 승인(구형 워크플로우)
        doc_reject_external         외부자 반려(구형 워크플로우)
        doc_request_internal        내부자 요청(구형 워크플로우)
        doc_accept_internal         내부자 승인(구형 워크플로우)
        doc_reject_internal         내부자 반려(구형 워크플로우)
        doc_tempsave_internal       내부자 임시 저장(구형 워크플로우)
        2. doc_complete                문서 완료
     */
    @Schema(description = "문서ID")  @JsonProperty("id") private String docId;
    @Schema(description = "문서타이틀")  @JsonProperty("document_title") private String documentTitle;
    @Schema(description = "템플릿ID")  @JsonProperty("template_id") private String templateId;
    @Schema(description = "템플릿명")  @JsonProperty("template_name") private String templateName;
    @Schema(description = "워크플로우 순번")  @JsonProperty("workflow_seq") private Integer workflowSeq;
    @Schema(description = "템플릿버전")  @JsonProperty("template_version") private String templateVersion;
    @Schema(description = "히스토리ID")  @JsonProperty("history_id") private String historyId;
    @Schema(description = "문서상태")  @JsonProperty("status") private String status;
    @Schema(description = "편집자ID")  @JsonProperty("editor_id") private String editorId;
    @Schema(description = "현재 시간(UTC Long)")  @JsonProperty("updated_date") private Long updatedDate;
    @Schema(description = "코멘트")  @JsonProperty("comment") private String comment;
    @Schema(description = "대량발송요청ID")  @JsonProperty("mass_job_request_id") private String massJobRequestId;
}
