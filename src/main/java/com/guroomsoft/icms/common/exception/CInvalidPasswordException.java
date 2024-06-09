package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CInvalidPasswordException extends RuntimeException {
    private static final int ERROR_CODE = -1012;
    private static final String KEY_MSG = "error.invalid.password";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CInvalidPasswordException(String msg, Throwable e){ super(msg, e);}
    public CInvalidPasswordException(String msg) { super(msg); }
    public CInvalidPasswordException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
