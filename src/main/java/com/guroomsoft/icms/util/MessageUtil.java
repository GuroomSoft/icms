package com.guroomsoft.icms.util;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageUtil {
    @Resource
    private MessageSource source;

    static MessageSource messageSource;

    @PostConstruct
    public void initialize(){
        messageSource = source;
    }

    public static String getMessage(String messageCode) {
        return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
    }

    public static String getMessage(String messageCode, Object[] messageArgs) {
        return messageSource.getMessage(messageCode, messageArgs, LocaleContextHolder.getLocale());
    }
}
