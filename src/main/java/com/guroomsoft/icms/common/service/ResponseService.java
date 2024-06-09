package com.guroomsoft.icms.common.service;

import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ResponseService {
    /**
     * 성공결과만 처리하는 메서드
     * @return
     */
    public CommonResult getSuccessResult() {
        CommonResult result = new CommonResult();
        result.setCode(0);
        result.setMessage("success");
        result.setType("info");
        return result;
    }

    /**
     * 실패 결과만 처리하는 메서드
     * @return
     */
    public CommonResult getFailResult() {
        CommonResult result = this.getFailResult(-1, "처리중 오류발생");
        return result;
    }

    public CommonResult getFailResult(int code, String msg) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMessage(msg);
        result.setType("error");
        return result;
    }

    public <T> DataResult<T> getDataResult(T data) {
        DataResult<T> result = new DataResult<>();
        result.setCode(0);
        result.setType("info");
        if (data == null) {
            result.setMessage("데이터 없음");
        } else {
            result.setMessage("성공");
        }
        result.setResult(data);
        return result;
    }
    public <T> DataResult<T> getDataResult(int code, String msg, T data) {
        DataResult<T> result = new DataResult<>();
        result.setCode(code);
        result.setMessage(msg);
        if (data == null) {
            result.setType("error");
        } else {
            result.setType("info");
        }
        result.setResult(data);
        return result;
    }


    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setResult(list);
        result.setCode(0);
        result.setType("info");
        if ((list == null) || list.isEmpty()) {
            result.setMessage("데이터 없음");
            result.setCount(0);
        } else {
            result.setMessage("성공");
            result.setCount(list.size());
        }
        return result;
    }

    public <T>ListResult<T> getListPageResult(List<T> list, int totalCount, int pageSize, int currentPage, int rowCountPerPage) {
        ListResult<T> result = new ListResult<>();
        result.setResult(list);
        result.setCode(0);
        result.setType("info");
        result.setTotalCount(totalCount);
        result.setPageSize(pageSize);
        result.setPage(currentPage);
        result.setRowCountPerPage(rowCountPerPage);

        if ((list == null) || list.isEmpty()) {
            result.setMessage("데이터 없음");
            result.setCount(0);
        } else {
            result.setMessage("성공");
            result.setCount(list.size());
        }
        return result;
    }

}
