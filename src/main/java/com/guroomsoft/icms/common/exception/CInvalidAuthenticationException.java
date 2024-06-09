package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CInvalidAuthenticationException extends RuntimeException {
    private static final int ERROR_CODE = -1008;
    private static final String KEY_MSG = "error.invalid.authentication";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CInvalidAuthenticationException(String msg, Throwable e){ super(msg, e);}
    public CInvalidAuthenticationException(String msg) { super(msg); }
    public CInvalidAuthenticationException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
