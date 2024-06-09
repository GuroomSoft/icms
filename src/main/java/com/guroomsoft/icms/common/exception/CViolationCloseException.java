package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CViolationCloseException extends RuntimeException {
    private static final int ERROR_CODE = -1015;
    private static final String KEY_MSG = "error.violation.close";

    public static int getCode(){
        return ERROR_CODE;
    }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CViolationCloseException(String msg, Throwable e){ super(msg, e);}
    public CViolationCloseException(String msg) { super(msg); }
    public CViolationCloseException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
