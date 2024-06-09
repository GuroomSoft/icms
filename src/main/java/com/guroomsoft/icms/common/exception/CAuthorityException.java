package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CAuthorityException extends RuntimeException {
    private static final int ERROR_CODE = -1006;
    private static final String KEY_MSG = "error.authority";

    public static int getCode(){
        return ERROR_CODE;
    }
    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CAuthorityException(String msg, Throwable e){ super(msg, e);}
    public CAuthorityException(String msg) { super(msg); }
    public CAuthorityException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
