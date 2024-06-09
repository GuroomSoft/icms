package com.guroomsoft.icms.biz.price.event;

import org.springframework.context.ApplicationEvent;

import java.util.List;

public class ClosePriceChangeDocEvent extends ApplicationEvent {
    private List<String> mDocList;

    public ClosePriceChangeDocEvent(Object source, List<String> docs)
    {
        super(source);
        this.mDocList = docs;
    }

    public List<String> getResponse()
    {
        return this.mDocList;
    }
}
