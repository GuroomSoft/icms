package com.guroomsoft.icms.biz.price.event;

import com.guroomsoft.icms.biz.agreement.service.AgreementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ClosePriceChangeDocListener {
    @Autowired
    private AgreementService agreementService;

    @Async("DefaultAsyncTaskPool")
    @EventListener
    public void onChangeStatusListener(ClosePriceChangeDocEvent evt)
    {
        log.debug("👉 Close PriceChange Doc event listener - start");
        List<String> docs = evt.getResponse();

        if (docs == null || docs.isEmpty()) {
            return;
        }

        for (String srcDocNo : docs) {
            try {
                agreementService.updateDetailImageString(srcDocNo);
            } catch (Exception e) {
                log.error("**** [ ClosePriceChangeDocListener ] Fail to update agreement detail {}", srcDocNo);
            }
        }
        log.debug("👉 Close PriceChange Doc event listener - end");

    }

}
