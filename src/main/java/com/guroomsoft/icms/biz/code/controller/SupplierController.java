package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.biz.code.service.CodeService;
import com.guroomsoft.icms.biz.code.service.CorporationService;
import com.guroomsoft.icms.biz.code.service.PlantService;
import com.guroomsoft.icms.biz.code.service.SupplierService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "협력사 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/supplier")
public class SupplierController {
    private final ResponseService responseService;
    private final SupplierService supplierService;
    private final CorporationService corporationService;
    private final PlantService plantService;

    private final CodeService codeService;

    @Operation(summary = "공급 협력사 목록 조회", description = "공급 협력사 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Partner> findSupplier(
            @Parameter(description = "회사코드") @RequestParam(required = false) String corpCd,
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "사업자번호") @RequestParam(required = false) String bizRegNo,
            @Parameter(description = "대표자명") @RequestParam(required = false) String ceoNm,
            @Parameter(description = "사용여부") @RequestParam(required = false) String useAt)
    {
        Partner cond = new Partner();
        if (StringUtils.isNotBlank(corpCd)) cond.setCorpCd(corpCd);
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(searchWord)) cond.setBpNm(searchWord);
        if (StringUtils.isNotBlank(bizRegNo)) cond.setBizRegNo(bizRegNo);
        if (StringUtils.isNotBlank(ceoNm)) cond.setCeoNm(ceoNm);
        if (StringUtils.isNotBlank(useAt)) cond.setUseAt(useAt);

        List<Partner> resultSet = null;
        try {
            resultSet = supplierService.findSupplier(cond);
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공급협력사 상세 조회", description = "공급협력사 상세 조회")
    @RequestMapping(value = "/{corpCd}/{plantCd}/{bpCd}", method = {RequestMethod.GET})
    public DataResult<Partner> findCustomerByKey(
            @Parameter(description = "회사 코드", required = true) @PathVariable String corpCd,
            @Parameter(description = "플랜트 코드", required = true) @PathVariable String plantCd,
            @Parameter(description = "BP 코드", required = true) @PathVariable String bpCd)
    {
        try {
            Partner resultSet = supplierService.findSupplierByKey(corpCd, plantCd, bpCd);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "공급 협력사 등록처리", description = "협력사 마스터에 등록된 데이터 중 공급사로 등록할 협력사를 선택하여 공급사로 등록처리, 플랜트 기준으로 등록")
    @RequestMapping(value = "/registration/{corpCd}/{plantCd}", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> registrationSupplier(
            @Parameter(description = "회사 코드", required = true) @PathVariable String corpCd,
            @Parameter(description = "플랜트 코드", required = true) @PathVariable String plantCd,
            @Parameter(description = "등록정보", required = true) @RequestBody List<Partner> dataList,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            for (Partner data : dataList) {
                data.setCorpCd(corpCd);
                data.setPlantCd(plantCd);
                data.setUseAt(AppContant.CommonValue.YES.getValue());
                data.setRegUid(reqUserUid);
            }
            Map<String, Object> result = supplierService.saveMultipleSupplier(dataList, reqUserUid, true);
            return responseService.getDataResult(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "공급협력사 일괄 등록처리", description = "고객협력사 일괄 등록처리")
    @RequestMapping(value = "/registration/multiple", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> registrationMultiSupplier(
            @Parameter(description = "등록정보", required = true) @RequestBody List<Partner> dataList,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Map<String, Object> resultMap = supplierService.saveMultipleSupplier(dataList, Long.valueOf(reqUserUid), false);
        return responseService.getDataResult(resultMap);
    }

    @Operation(summary = "공급협력사 수정 처리", description = "공급협력사 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifySupplier(
            @Parameter(description = "등록정보", required = true) @RequestBody Partner data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int updated = supplierService.modifySupplier(data, Long.valueOf(reqUserUid));
            if (updated == 0) {
                return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
            } else {
                return responseService.getSuccessResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CModifictionFailException e) {
            return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공급협력사 미사용 처리", description = "고객협력사 미사용 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removeSupplier(
            @Parameter(description = "회사 코드", required = true) @RequestParam String corpCd,
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(description = "BP 코드", required = true) @RequestParam String bpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = supplierService.disableSupplier(corpCd, plantCd, bpCd, Long.valueOf(reqUserUid));
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CRemoveFailException e) {
            return responseService.getDataResult(CRemoveFailException.getCode(), CRemoveFailException.getCustomMessage(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "협력업체 대장 엑셀 내보내기", description = "협력업체 대장 엑셀 내보내기")
    @RequestMapping(value = "/exportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelForSupplier(
            @Parameter(description = "회사코드") @RequestParam(required = false) String corpCd,
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "사용여부") @RequestParam(required = false) String useAt)

    {
        Partner cond = new Partner();
        String corpNm = "전체", plantNm = "전체";

        if (StringUtils.isNotBlank(corpCd)) {
            cond.setCorpCd(corpCd);
            corpNm = String.format("[%s] %s", corpCd, corporationService.getName(corpCd));
        }
        if (StringUtils.isNotBlank(plantCd)) {
            cond.setPlantCd(plantCd);
            plantNm = String.format("[%s] %s", plantCd, plantService.getName(plantCd));
        }
        if (StringUtils.isNotBlank(searchWord)) { cond.setBpNm(searchWord);}
        if (StringUtils.isNotBlank(useAt)) { cond.setUseAt(useAt); }

        try {
            List<Partner> resultList = supplierService.findSupplier(cond);
            String reportTitle = "공급협력사 대장";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());
            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("cond0", String.format("[회사] : %s", StringUtils.defaultString(corpNm)));
            condMap.put("cond1", String.format("[플랜트] : %s", StringUtils.defaultString(plantNm)));
            condMap.put("cond2", String.format("[검색어] : %s", StringUtils.defaultString( searchWord, "전체" )));
            condMap.put("cond3", String.format("[사용여부] : %s",
                    StringUtils.defaultString( codeService.getCodeNameByKey("C03", useAt), "전체" )
            ));

            String[] colHeaders = {"No", "회사코드", "회사", "플랜트코드", "플랜트", "회사", "코드", "고객사", "약식명", "대표자", "사업자번호", "이메일", "전화번호",
                    "우편번호", "주소", "담당자명", "담당자 이메일", "담당자 전화", "사용여부", "노출여부", "비고", "등록일시", "등록자"};

            return supplierService.exportToExcelForSupplier(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "SAP 로부터 공급협력사 다운로드", description = "SAP 로부터 공급협력사 다운로드")
    @RequestMapping(value = "/downloadFromSAP", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> downloadFromSAP(
            @Parameter(description = "회사코드", required = true) @RequestParam String corpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int resultCount = supplierService.downloadSupplierFromSap(corpCd, reqUserUid);
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", Integer.valueOf(resultCount));
            return responseService.getDataResult(result);
        } catch (Exception e) {
            return responseService.getDataResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage(), null);
        }
    }


    @Operation(summary = "신규 추가 공급 협력사 목록 조회", description = "신규 추가 공급 협력사 목록 조회")
    @RequestMapping(value = "/helperForNew", method = {RequestMethod.GET})
    public ListResult<Partner> findSupplierForNew(
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord)
    {
        List<Partner> resultSet = null;
        try {
            resultSet = supplierService.helperSupplierForNew(plantCd, searchWord, AppContant.CommonValue.YES.getValue());
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }


}
