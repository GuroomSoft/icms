package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CAlreadyCloseException extends RuntimeException {
    private static final int ERROR_CODE = -1016;
    private static final String KEY_MSG = "error.already.close";

    public static int getCode(){
        return ERROR_CODE;
    }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CAlreadyCloseException(String msg, Throwable e){ super(msg, e);}
    public CAlreadyCloseException(String msg) { super(msg); }
    public CAlreadyCloseException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
