package com.guroomsoft.icms.common.exception;

import com.guroomsoft.icms.util.MessageUtil;

public class CRegistrationFailException extends RuntimeException {
    private static final int ERROR_CODE = -1012;
    private static final String KEY_MSG = "error.registration.fail";

    public static int getCode(){
        return ERROR_CODE;
    }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CRegistrationFailException(String msg, Throwable e){ super(msg, e);}
    public CRegistrationFailException(String msg) { super(msg); }
    public CRegistrationFailException() { super(MessageUtil.getMessage(KEY_MSG)); }

}
