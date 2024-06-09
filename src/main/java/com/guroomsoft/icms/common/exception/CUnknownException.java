package com.guroomsoft.icms.common.exception;


import com.guroomsoft.icms.util.MessageUtil;

public class CUnknownException extends RuntimeException {
    private static final int ERROR_CODE = -9999;
    private static final String KEY_MSG = "error.unknown";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CUnknownException(String msg, Throwable e){ super(msg, e);}
    public CUnknownException(String msg) { super(msg); }

    public CUnknownException() {
        super(MessageUtil.getMessage(KEY_MSG));
    }

}
