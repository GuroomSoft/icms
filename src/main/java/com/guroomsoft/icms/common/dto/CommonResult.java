package com.guroomsoft.icms.common.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonResult implements Serializable {
    private static final long serialVersionUID = 174726374856738L;
    private int code;
    private String message;
    private String type;
}
