package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CAlreadyExistException extends RuntimeException {
    private static final int ERROR_CODE = -1001;
    private static final String KEY_MSG = "error.already.exist";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CAlreadyExistException(String msg, Throwable e){ super(msg, e);}
    public CAlreadyExistException(String msg) { super(msg); }
    public CAlreadyExistException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
