package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CModifictionFailException extends RuntimeException {
    private static final int ERROR_CODE = -1013;
    private static final String KEY_MSG = "error.modification.fail";

    public static int getCode(){
        return ERROR_CODE;
    }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CModifictionFailException(String msg, Throwable e){ super(msg, e);}
    public CModifictionFailException(String msg) { super(msg); }
    public CModifictionFailException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
