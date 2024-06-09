package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CAuthenticationException extends RuntimeException {
    private static final int ERROR_CODE = -1000;
    private static final String KEY_MSG = "error.authentication";

    public static int getCode(){
        return ERROR_CODE;
    }
    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CAuthenticationException(String msg, Throwable e){ super(msg, e);}
    public CAuthenticationException(String msg) { super(msg); }
    public CAuthenticationException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
