package com.guroomsoft.icms.biz.material.event;

import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestDocEventListener {

    @Async("DefaultAsyncTaskPool")
    @EventListener
    public void listenerTestDoc(TestDocEvent evt)
    {
        log.info("👉 Test Doc event listener - start");
        AnnouncePrice doc = evt.getDoc();

        if (doc == null || StringUtils.isBlank(doc.getDocNo()))
        {
            log.info("👉 document is empty");
            return ;
        }

        log.info("👉 Test Doc event listener - end");
    }
}
