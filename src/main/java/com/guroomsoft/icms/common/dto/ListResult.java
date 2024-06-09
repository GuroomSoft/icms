package com.guroomsoft.icms.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListResult<T> extends CommonResult implements Serializable {
    private int pageSize;       // 전체 페이지 크기
    private int page;           // 현재 페이지번호
    private int totalCount;     // 전체 건수
    private int count;          // 현재 페이지 건수
    private int rowCountPerPage;     // 페이지당 건수

    private List<T> result;

}
