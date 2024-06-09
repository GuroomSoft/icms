package com.guroomsoft.icms.biz.agreement.event;

import com.guroomsoft.icms.biz.agreement.dao.AgreementDAO;
import com.guroomsoft.icms.biz.agreement.dto.WebhookResponse;
import com.guroomsoft.icms.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class EformsignStatusUpdateListener {
    @Autowired
    private AgreementDAO agreementDAO;

    @Async("DefaultAsyncTaskPool")
    @EventListener
    public void onChangeStatusListener(EformsignStatusUpdateEvent evt)
    {
        log.debug("👉 Eformsign status event listener - start");

        WebhookResponse res = evt.getResponse();

        if (res == null || res.getDocument() == null) {
            return;
        }
        Map<String, Object> paramMap = new LinkedHashMap<>();
        int errorCode = -1;

        if (res.getEventType().equalsIgnoreCase("document")) {
            try {
                paramMap = convertMapFromResponse(evt.getResponse());
                agreementDAO.insertEformWebhook(paramMap);
                errorCode = ((Integer) paramMap.get("errCode")).intValue();
                if (errorCode < 0) {
                    log.debug("👉 onChangeStatusListener 등록을 실패하였습니다.");
                }

                log.debug("👉 onChangeStatusListener {}", evt.getResponse().toString());
            } catch (Exception e) {
                log.debug("👉 onChangeStatusListener {}", e.getMessage());
            }
//        } else if ( res.getEventType().equalsIgnoreCase("document_action") ) {
//        } else if ( res.getEventType().equalsIgnoreCase("ready_document_pdf") ) {
        } else {
            log.debug("👉 onChangeStatusListener {}", evt.getResponse().toString());
        }

        log.debug("👉 Eformsign status event listener - end");
    }

    private Map<String, Object> convertMapFromResponse(WebhookResponse res)
    {
        String updateDt = convertUTCtoString(res.getDocument().getUpdatedDate().longValue());

        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap.put("pWebhookId", res.getWebhookId());
        resultMap.put("pWebhookName", res.getWebhookName());
        resultMap.put("pCompanyId", res.getCompanyId());
        resultMap.put("pEventType", res.getEventType());
        resultMap.put("pDocId", res.getDocument().getDocId());
        resultMap.put("pDocTitle", res.getDocument().getDocumentTitle());

        resultMap.put("pTemplateId", res.getDocument().getTemplateId());
        resultMap.put("pTemplateName", res.getDocument().getTemplateName());
        resultMap.put("pTemplateVersion", res.getDocument().getTemplateVersion());
        resultMap.put("pWorkflowSeq", res.getDocument().getWorkflowSeq());
        resultMap.put("pHistoryId", res.getDocument().getHistoryId());
        resultMap.put("pDocStatus", res.getDocument().getStatus());
        resultMap.put("pEditorId", res.getDocument().getEditorId());
        resultMap.put("pUpdateDate", res.getDocument().getUpdatedDate());
        resultMap.put("pUpdateDt", StringUtils.defaultString(updateDt));
        resultMap.put("pMassJobRequestId", res.getDocument().getMassJobRequestId());
        resultMap.put("pComment", res.getDocument().getComment());
        return resultMap;
    }

    private String convertUTCtoString(long param)
    {
        String updateDt = "";
        try {
            Instant t = Instant.ofEpochMilli(param);
            LocalDateTime dt = t.atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
            updateDt = DateTimeUtil.datetimeToString(dt);
            return updateDt;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }
}
