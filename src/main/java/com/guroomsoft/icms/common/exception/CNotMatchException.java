package com.guroomsoft.icms.common.exception;


import com.guroomsoft.icms.util.MessageUtil;

public class CNotMatchException extends RuntimeException {
    private static final int ERROR_CODE = -1010;
    public static final String KEY_MSG = "error.notmatch";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG, new String[]{"데이터"});
    }

    public CNotMatchException(String msg, Throwable e){ super(msg, e);}
    public CNotMatchException(String msg) { super(msg); }

    public CNotMatchException() {
        super(MessageUtil.getMessage(KEY_MSG, new String[]{"데이터"}));
    }

}
