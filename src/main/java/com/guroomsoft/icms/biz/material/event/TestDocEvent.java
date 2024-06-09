package com.guroomsoft.icms.biz.material.event;

import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import org.springframework.context.ApplicationEvent;

public class TestDocEvent extends ApplicationEvent {
    private AnnouncePrice mDoc;

    public TestDocEvent(Object source, AnnouncePrice doc)
    {
        super(source);
        this.mDoc = doc;
    }

    public AnnouncePrice getDoc()
    {
        return this.mDoc;
    }
}
