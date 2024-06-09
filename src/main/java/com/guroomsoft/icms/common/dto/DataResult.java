package com.guroomsoft.icms.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataResult<T> extends CommonResult implements Serializable {
    private T result;
}
