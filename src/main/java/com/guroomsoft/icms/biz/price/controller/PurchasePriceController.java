package com.guroomsoft.icms.biz.price.controller;

import com.guroomsoft.icms.biz.code.service.PlantService;
import com.guroomsoft.icms.biz.price.dto.PurchasePrice;
import com.guroomsoft.icms.biz.price.dto.PurchasePriceReq;
import com.guroomsoft.icms.biz.price.service.PurchasePriceService;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CDatabaseException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Tag(name = "공급업체 사급단가 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/price/purchase")
public class PurchasePriceController {
    private final ResponseService responseService;
    private final PurchasePriceService purchasePriceService;
    private final PlantService plantService;
    private final AppConfigService appConfigService;

    @Operation(summary = "공급사 사급단가 목록 조회", description = "공급사 사급단가 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    public ListResult<PurchasePrice> findPurchasePrice(
            @Parameter(description = "플랜트 코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "고객사 코드") @RequestParam(required = false) String bpCd,
            @Parameter(description = "검색할 재료코드 또는 재료명") @RequestParam(required = false) String searchWord,
            @Parameter(description = "페이지번호", required = true) @RequestParam(defaultValue = "1") int pageNumber,
            @Parameter(description = "페이지당 행수", required = true) @RequestParam(defaultValue = "20") int rowCountPerPage)
    {
        PurchasePriceReq cond = new PurchasePriceReq();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(bpCd)) cond.setBpCd(bpCd);
        if (StringUtils.isNotBlank(searchWord)) cond.setSearchWord(searchWord);

        List<PurchasePrice> resultSet = null;
        try{
            // 디폴트 페이지 당 조회 건수
            if (rowCountPerPage == 0) {
                rowCountPerPage = appConfigService.getIntValue(AppConfigService.ROWCOUNT_PER_PAGE);
            }
            cond.setRowCountPerPage(rowCountPerPage);
            cond.setTotalCount( purchasePriceService.getPurchasePriceCount(cond));
            int rc = cond.getRowCountPerPage();
            int share = cond.getTotalCount() / rc;
            int remainder = cond.getTotalCount() % rc;

            if (remainder > 0) share++;
            cond.setTotalPageCount(share);
            cond.setPageNumber(pageNumber);

            // 조회 요청 페이지가 전체 페이지수 보다 크면 빈 목록 반환
            if (cond.getTotalPageCount() < cond.getPageNumber()) {
                return responseService.getListPageResult(null, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
            }

            cond.setStartOffset((cond.getPageNumber()-1) * rowCountPerPage);
            resultSet = purchasePriceService.findPurchasePrice(cond);
            return responseService.getListPageResult(resultSet, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "SAP 로부터 공급사 사급단가 적재", description = "SAP 로부터 공급사 사급단가 적재")
    @RequestMapping(value = "/downloadFromSAP", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> downloadFromSAP(
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(description = "협력사 코드") @RequestParam (required = false) String bpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        List<String> plants = new ArrayList<>();
        plants.add(plantCd);

        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        try {
            params.put("plants", plants);
            if (StringUtils.isBlank(bpCd)) params.put("bpCd", bpCd);

            int resultCount = purchasePriceService.downloadPurchasePriceFromSap(params, reqUserUid);
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", Integer.valueOf(resultCount));
            return responseService.getDataResult(result);
        } catch (Exception e) {
            return responseService.getDataResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "공급사 사급단가 엑셀 내보내기", description = "공급사 사급단가 엑셀 내보내기")
    @RequestMapping(value = "/exportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelForPurchasePrice(
            @Parameter(description = "플랜트 코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "공급사 코드") @RequestParam(required = false) String bpCd,
            @Parameter(description = "검색할 재료코드 또는 재료명") @RequestParam(required = false) String searchWord)
    {
        PurchasePriceReq cond = new PurchasePriceReq();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(bpCd)) cond.setBpCd(bpCd);
        if (StringUtils.isNotBlank(searchWord)) cond.setSearchWord(searchWord);

        try {
            List<PurchasePrice> resultList = purchasePriceService.findPurchasePrice(cond);
            String reportTitle = "협력사 매입단가 목록";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());
            String plantNm = plantService.getName(plantCd);

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("cond0", String.format("[플랜트코드] : [%s] %s",
                    StringUtils.defaultString(plantCd), StringUtils.defaultString(plantNm)));
            condMap.put("cond1", String.format("[공급사] : %s", StringUtils.defaultString(bpCd, "전체" )));
            condMap.put("cond2", String.format("[검색자재] : %s", StringUtils.defaultString(searchWord, "전체" )));

            String[] colHeaders = {"No", "구매조직", "플랜트코드", "플랜트", "코드", "공급사", "자재코드", "자재명", "단가", "가격결정단위",
                    "적용단가", "통화단위", "구매단위", "단가상태", "등록일시", "등록자"};

            return purchasePriceService.exportToExcelForPurchasePrice(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
