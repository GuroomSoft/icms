package com.guroomsoft.icms.biz.material.event;

import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import com.guroomsoft.icms.biz.price.service.ChangePriceService;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class CloseAnnouncePriceDocEventListener {
    @Autowired
    private ChangePriceService changePriceService;

    @Async("DefaultAsyncTaskPool")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void listenerCloseAnnouncePriceDoc(CloseAnnouncePriceDocEvent evt)
    {
        log.debug("👉 Close announce price doc event listener - start");
        AnnouncePrice doc = evt.getDoc();

        if (doc == null || StringUtils.isBlank(doc.getDocNo()))
        {
            log.info("👉 document is empty");
            return ;
        }

        // 가격합의서 처리
        log.info("👉 Close {}", evt.getDoc() );
        log.info(evt.getDoc().toString());
        try {
            changePriceService.createPriceChangeAll(evt.getDoc().getDocNo(), evt.getDoc().getRegUid());
        } catch (CBizProcessFailException e) {
            throw new CBizProcessFailException(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
        log.debug("👉 Close announce price doc event listener - end");
    }
}
