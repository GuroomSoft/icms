package com.guroomsoft.icms.common.exception;


import com.guroomsoft.icms.util.MessageUtil;

public class CFileStorageException extends RuntimeException {
    private static final int ERROR_CODE = -1011;
    private static final String KEY_MSG = "error.creat.folder";

    public static int getCode(){ return ERROR_CODE; }

    public static String getCustomMessage() {
        return MessageUtil.getMessage(KEY_MSG);
    }

    public CFileStorageException(String msg, Throwable e){ super(msg, e);}
    public CFileStorageException(String msg) { super(msg); }

    public CFileStorageException() {
        super(MessageUtil.getMessage(KEY_MSG));
    }

}
