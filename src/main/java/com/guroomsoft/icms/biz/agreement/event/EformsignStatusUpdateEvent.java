package com.guroomsoft.icms.biz.agreement.event;

import com.guroomsoft.icms.biz.agreement.dto.WebhookResponse;
import org.springframework.context.ApplicationEvent;

public class EformsignStatusUpdateEvent extends ApplicationEvent {
    private WebhookResponse mRes;

    public EformsignStatusUpdateEvent(Object source, WebhookResponse res)
    {
        super(source);
        this.mRes = res;
    }

    public WebhookResponse getResponse()
    {
        return this.mRes;
    }
}
