package com.guroomsoft.icms.biz.material.event;

import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import org.springframework.context.ApplicationEvent;

public class CloseAnnouncePriceDocEvent extends ApplicationEvent {
    private AnnouncePrice doc;

    public CloseAnnouncePriceDocEvent(Object source, AnnouncePrice doc)
    {
        super(source);
        this.doc = doc;
    }

    public AnnouncePrice getDoc()
    {
        return this.doc;
    }
}
