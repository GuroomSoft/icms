package com.guroomsoft.icms.biz.price.controller;

import com.guroomsoft.icms.biz.code.service.PlantService;
import com.guroomsoft.icms.biz.price.dto.ChangePrice;
import com.guroomsoft.icms.biz.price.dto.DetailReq;
import com.guroomsoft.icms.biz.price.dto.PurchaseItemReq;
import com.guroomsoft.icms.biz.price.service.ChangePriceService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.DateTimeUtil;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "가격변경관리 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/price/change")
public class ChangePriceController {
    private final ResponseService responseService;
    private final ChangePriceService changePriceService;
    private final PlantService plantService;

    @Operation(summary = "가격변경 상세 목록 조회", description = "가격변경 상세 목록 조회")
    @RequestMapping(value = "/detail", method = {RequestMethod.POST})
    public ListResult<ChangePrice> findChangePriceList(
            @Parameter(description = "조회조건", required = true) @RequestBody DetailReq cond)
    {
        List<ChangePrice> resultSet = null;
        try {
            if (cond.getBpList() != null && cond.getBpList().isEmpty()) cond.setBpList(null);
            resultSet = changePriceService.findChangedPrice(cond);

            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "협력사 목록 조회", description = "협력사 목록 조회")
    @RequestMapping(value = "/targetBp", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> findTargetBp(
            @Parameter(description = "고시일(YYYYMMDD)", required = true) @RequestParam String announcedDate,
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd)
    {
        List<Map<String, Object>> resultSet = null;
        try {
            resultSet = changePriceService.findTargetBpList(announcedDate, plantCd);
            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "매입품목 기준 가격변경 조회", description = "매입품목 기준 가격변경 목록 조회")
    @RequestMapping(value = "/purchaseItem", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> findPurchaseItemList(
            @Parameter(description = "조회조건", required = true) @RequestBody PurchaseItemReq search)
    {
        if (search.getBpList() != null && search.getBpList().isEmpty()) search.setBpList(null);

        try {
            List<Map<String, Object>> resultSet = changePriceService.findChangePricePurchaseItem(search);
            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "가격결정합의 내용 조회", description = "가격결정합의 내용 조회")
    @RequestMapping(value = "/agreement/content", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> getArgeementContent(
            @Parameter(description = "등록월", required = true) @RequestParam String announcedDate,
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(description = "협력사 코드", required = true) @RequestParam String bpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        PurchaseItemReq cond = new PurchaseItemReq();
        cond.setAnnouncedDate(announcedDate);
        cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(bpCd)) cond.setBpCd(bpCd);

        try {
            Map<String, Object> resultSet = changePriceService.getAgreementContent(cond, Long.valueOf(reqUserUid));
            return responseService.getDataResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "플랜트별 가격 변경 계산 처리 후 가격결정합의서 생성", description = "플랜트별 가격 변경 계산 처리 후 가격결정합의서 생성")
    @RequestMapping(value = "/calculate", method = {RequestMethod.POST})
    public CommonResult calculateChangePrice(
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(description = "등록월", required = true) @RequestParam String announcedDate,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            //changePriceService.calculateChangePrice(plantCd, announcedDate.replaceAll("[^0-9]", ""), Long.valueOf(reqUserUid));
            changePriceService.calculateChangePrice(plantCd, announcedDate, Long.valueOf(reqUserUid));
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CBizProcessFailException e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "가격변경 확정 처리", description = "사용자가 지정한 문서번호 목록을 기준으로 가격변경 확정 처리")
    @RequestMapping(value = "/confirm", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> confirmPriceChange(
            @Parameter(description = "확정 문서번호 목록", required = true) @RequestBody List<String> docs,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (docs == null || docs.isEmpty())
            {
                resultMap.put("totalCount", Integer.valueOf(0));
                resultMap.put("successCount", Integer.valueOf(0));
                resultMap.put("failCount", Integer.valueOf(0));
            }

            int successCount = changePriceService.confirmPriceChangeDoc(docs, Long.valueOf(reqUserUid));

            resultMap.put("totalCount", docs.size());
            resultMap.put("successCount", Integer.valueOf(successCount));
            resultMap.put("failCount", docs.size() - successCount);
            return responseService.getDataResult(resultMap);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (SQLServerException e) {
            return responseService.getDataResult(CDatabaseException.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "SAP 매입 단가 엑셀 내보내기", description = "SAP 매입 단가 엑셀 내보내기")
    @RequestMapping(value = "/SapExportToExcelPurchase", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelForPurchaseSap(
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd)
    {
        ChangePrice cond = new ChangePrice();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);

        try {
            List<ChangePrice> resultSet = changePriceService.findPurchaseForSap(cond);
            String reportTitle = "매입단가 업로드 작성";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            //if (StringUtils.isNotBlank(plantCd)) {
            //    condMap.put("cond0", "[플랜트] : 전체");
            //} else {
            //    condMap.put("cond0", String.format("[플랜트] : %s - %s", plantCd, plantService.getName(plantCd)));
            //}

            String[] colHeaders = {"소급반영일", "구매그룹", "품의번호", "공급업체코드", "공급업체명", "자재코드", "단위"
                    , "통화", "신규단가", "단가유형", "적용일자", "정산단가", "기간(FROM)1", "기간(TO)1", "정산사유1", "정산단가1"
            };

            return changePriceService.exportToExcelForPurchaseSap(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultSet);
        } catch (CNotFoundException e) {
            throw new CNotFoundException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "SAP 사급 단가 엑셀 내보내기", description = "SAP 사급 단가 엑셀 내보내기")
    @RequestMapping(value = "/SapExportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelForSap(
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd)
    {
        ChangePrice cond = new ChangePrice();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);

        try {
            List<ChangePrice> resultSet = changePriceService.findConsignedForSap(cond);
            String reportTitle = "사급단가 업로드 작성";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            //if (StringUtils.isNotBlank(plantCd)) {
            //    condMap.put("cond0", "[플랜트] : 전체");
            //} else {
            //    condMap.put("cond0", String.format("[플랜트] : %s - %s", plantCd, plantService.getName(plantCd)));
            //}

            String[] colHeaders = {"필드명", "영업조직", "영업조직명", "유통채널", "유통채널명", "판매처", "차종"
                    , "차종명", "자재", "자재명", "통화", "종전판가", "신규판가", "판가차이", "기준수량", "판가유형", "적용관세"
                    , "적용일자", "PLANT", "소급유형(1)", "시작일(1)", "종료일(1)", "소급단가(1)"
            };

            return changePriceService.exportToExcelForSap(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultSet);
        } catch (CNotFoundException e) {
            throw new CNotFoundException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

}
