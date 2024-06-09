package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.biz.code.dto.Item;
import com.guroomsoft.icms.biz.code.dto.ItemReq;
import com.guroomsoft.icms.biz.code.service.ItemService;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CNotFoundException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@Tag(name = "품목정보 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/item")
public class ItemController {
    private final ResponseService responseService;
    private final ItemService itemService;
    private final AppConfigService appConfigService;

    @Operation(summary = "품목 목록 조회", description = "품목 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Item> findItem(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "원소재코드") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "재질코드") @RequestParam(required = false) String materialCd,
            @Parameter(description = "고객사재질코드") @RequestParam(required = false) String customerMatCd,
            @Parameter(description = "페이지번호", required = true) @RequestParam(defaultValue = "1") int pageNumber,
            @Parameter(description = "페이지당 행수", required = true) @RequestParam(defaultValue = "20") int rowCountPerPage)

    {
        ItemReq cond = new ItemReq();
        if (StringUtils.isNotBlank(searchWord)) cond.setSearchWord(searchWord);
        if (StringUtils.isNotBlank(materialCd)) cond.setMaterialCd(materialCd);
        if (StringUtils.isNotBlank(rawMaterialCd)) cond.setRawMaterialCd(rawMaterialCd);
        if (StringUtils.isNotBlank(customerMatCd)) cond.setCustomerMatCd(customerMatCd);
        cond.setPageNumber(pageNumber);

        List<Item> resultSet = null;
        try {
            // 디폴트 페이지 당 조회 건수
            if (rowCountPerPage == 0) {
                rowCountPerPage = appConfigService.getIntValue(AppConfigService.ROWCOUNT_PER_PAGE);
            }
            cond.setRowCountPerPage(rowCountPerPage);
            if (cond.getRowCountPerPage() > 0) {
                cond.setTotalCount( itemService.getTotalItemCount(cond) );
                int rc = cond.getRowCountPerPage();
                int share = cond.getTotalCount() / rc;
                int remainder = cond.getTotalCount() % rc;

                if (remainder > 0) share++;

                cond.setTotalPageCount(share);
            }

            cond.setPageNumber(pageNumber);
            // 조회 요청 페이지가 전체 페이지수 보다 크면 빈 목록 반환
            if (cond.getTotalPageCount() < cond.getPageNumber()) {
                return responseService.getListPageResult(null, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
            }

            cond.setStartOffset((cond.getPageNumber()-1) * rowCountPerPage);
            resultSet = itemService.findItem(cond);
            return responseService.getListPageResult(resultSet, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "SAP 로부터 품목정보 적재", description = "SAP 로부터 품목정보 적재")
    @RequestMapping(value = "/downloadFromSAP", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> downloadFromSAP(
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        try {
            params.put("plantCd", plantCd);

            int resultCount = itemService.downloadItemFromSap(params, reqUserUid);
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", Integer.valueOf(resultCount));
            return responseService.getDataResult(result);
        } catch (Exception e) {
            return responseService.getDataResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage(), null);
        }
    }


}
