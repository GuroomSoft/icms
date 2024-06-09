package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CTokenValidException extends RuntimeException {
    private static final int ERROR_CODE = -1005;
    private static final String KEY_MSG = "error.token.valid";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CTokenValidException(String msg, Throwable e){ super(msg, e);}
    public CTokenValidException(String msg) { super(msg); }
    public CTokenValidException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
