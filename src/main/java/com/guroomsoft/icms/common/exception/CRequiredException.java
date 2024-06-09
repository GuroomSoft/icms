package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CRequiredException extends RuntimeException {
    private static final int ERROR_CODE = -1004;
    private static final String KEY_MSG = "error.required";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CRequiredException(String msg, Throwable e){ super(msg, e);}
    public CRequiredException(String msg) { super(msg); }

    public CRequiredException() {
        super(MessageUtil.getMessage(KEY_MSG));
    }

}
