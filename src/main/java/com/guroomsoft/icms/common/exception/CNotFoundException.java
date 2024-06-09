package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CNotFoundException extends RuntimeException {
    private static final int ERROR_CODE = -1009;
    public static final String KEY_MSG = "error.notfound";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG, new String[]{"데이터"});
    }

    public CNotFoundException(String msg, Throwable e){ super(msg, e);}
    public CNotFoundException(String msg) { super(msg); }

    public CNotFoundException() {
        super(MessageUtil.getMessage(KEY_MSG, new String[]{"데이터"}));
    }

}
