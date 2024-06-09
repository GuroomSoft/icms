package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CRemoveFailException extends RuntimeException {
    private static final int ERROR_CODE = -1014;
    private static final String KEY_MSG = "error.remove.fail";

    public static int getCode(){
        return ERROR_CODE;
    }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CRemoveFailException(String msg, Throwable e){ super(msg, e);}
    public CRemoveFailException(String msg) { super(msg); }
    public CRemoveFailException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
