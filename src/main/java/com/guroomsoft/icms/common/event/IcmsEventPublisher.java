package com.guroomsoft.icms.common.event;

import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.event.RegisterUserEvent;
import com.guroomsoft.icms.biz.agreement.dto.WebhookResponse;
import com.guroomsoft.icms.biz.agreement.event.EformsignStatusUpdateEvent;
import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import com.guroomsoft.icms.biz.material.event.CloseAnnouncePriceDocEvent;
import com.guroomsoft.icms.biz.price.event.ClosePriceChangeDocEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class IcmsEventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCloseAnnouncePriceDocEvent(final AnnouncePrice msg)
    {
        log.debug("👉 Publishing Announce Price Document Close Event");
        CloseAnnouncePriceDocEvent closeEvent = new CloseAnnouncePriceDocEvent(this, msg);
        applicationEventPublisher.publishEvent(closeEvent);
    }

    public void publishUpdateEfomsignStatus(final WebhookResponse msg)
    {
        log.debug("👉 Publishing Update eformsing status update Event");
        EformsignStatusUpdateEvent evt = new EformsignStatusUpdateEvent(this, msg);
        applicationEventPublisher.publishEvent(evt);
    }

    public void publishRegisterUser(final User msg)
    {
        log.debug("👉 Publishing Update User status update Event");
        RegisterUserEvent evt = new RegisterUserEvent(this, msg);
        applicationEventPublisher.publishEvent(evt);
    }

    public void publishClosePriceChange(final List<String> msg)
    {
        log.debug("👉 Publishing Close Price change Event");
        ClosePriceChangeDocEvent evt = new ClosePriceChangeDocEvent(this, msg);
        applicationEventPublisher.publishEvent(evt);
    }

}
