package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.biz.code.service.CodeService;
import com.guroomsoft.icms.biz.code.service.CorporationService;
import com.guroomsoft.icms.biz.code.service.CustomerService;
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
@Tag(name = "판매처 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final ResponseService responseService;
    private final CustomerService customerService;
    private final CorporationService corporationService;

    private final CodeService codeService;

    @Operation(summary = "고객사 목록 조회", description = "고객사 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Partner> findCustomer(
            @Parameter(description = "회사코드") @RequestParam(required = false) String corpCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "사업자번호") @RequestParam(required = false) String bizRegNo,
            @Parameter(description = "대표자명") @RequestParam(required = false) String ceoNm,
            @Parameter(description = "사용여부") @RequestParam(required = false) String useAt)
    {
        Partner cond = new Partner();
        if (StringUtils.isNotBlank(corpCd)) cond.setCorpCd(corpCd);
        if (StringUtils.isNotBlank(searchWord)) cond.setBpNm(searchWord);
        if (StringUtils.isNotBlank(bizRegNo)) cond.setBizRegNo(bizRegNo);
        if (StringUtils.isNotBlank(ceoNm)) cond.setCeoNm(ceoNm);
        if (StringUtils.isNotBlank(useAt)) cond.setUseAt(useAt);

        List<Partner> resultSet = null;
        try {
            resultSet = customerService.findCustomer(cond);
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "고객사 상세 조회", description = "고객사 상세 조회")
    @RequestMapping(value = "/{corpCd}/{bpCd}", method = {RequestMethod.GET})
    public DataResult<Partner> findCustomerByKey(
            @Parameter(description = "회사 코드", required = true) @PathVariable String corpCd,
            @Parameter(description = "BP 코드", required = true) @PathVariable String bpCd)
    {
        try {
            Partner resultSet = customerService.findCustomerByKey(corpCd, bpCd);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "고객사 등록처리", description = "고객사 등록처리")
    @RequestMapping(value = "/registration/{corpCd}", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> registrationCustomer(
            @Parameter(description = "회사 코드", required = true) @PathVariable String corpCd,
            @Parameter(description = "등록정보", required = true) @RequestBody List<Partner> dataList,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            for (Partner data : dataList) {
                data.setCorpCd(corpCd);
                data.setUseAt(AppContant.CommonValue.YES.getValue());
                data.setRegUid(reqUserUid);
            }
            Map<String, Object> result = customerService.saveMultipleCustomer(dataList, Long.valueOf(reqUserUid), true);
            return responseService.getDataResult(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "고객사 일괄 등록처리", description = "고객사 일괄 등록처리")
    @RequestMapping(value = "/registration/multiple", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> registrationMultiCustomer(
            @Parameter(description = "등록정보", required = true) @RequestBody List<Partner> dataList,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Map<String, Object> resultMap = customerService.saveMultipleCustomer(dataList, Long.valueOf(reqUserUid), false);
        return responseService.getDataResult(resultMap);
    }

    @Operation(summary = "고객사 수정 처리", description = "고객사 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifyCustomer(
            @Parameter(description = "등록정보", required = true) @RequestBody Partner data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int updated = customerService.modifyCustomer(data, Long.valueOf(reqUserUid));
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

    @Operation(summary = "고객사 삭제 처리", description = "고객사 미사용 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removeCustomer(
            @Parameter(description = "회사 코드", required = true) @RequestParam String corpCd,
            @Parameter(description = "BP 코드", required = true) @RequestParam String bpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = customerService.disableCustomer(corpCd, bpCd, Long.valueOf(reqUserUid));
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
    public ResponseEntity<byte[]> exportToExcelForCustomer(
            @Parameter(description = "회사코드") @RequestParam(required = false) String corpCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "사용여부") @RequestParam(required = false) String useAt)
    {
        Partner cond = new Partner();
        if (StringUtils.isNotBlank(corpCd)) { cond.setCorpCd(corpCd);}
        if (StringUtils.isNotBlank(searchWord)) { cond.setBpNm(searchWord);}
        if (StringUtils.isNotBlank(useAt)) { cond.setUseAt(useAt); }

        try {
            List<Partner> resultList = customerService.findCustomer(cond);
            String reportTitle = "거래처대장";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());
            String corpNm = corporationService.getName(corpCd);

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("cond0", String.format("[회사코드] : [%s] %s",
                    StringUtils.defaultString(corpCd), StringUtils.defaultString(corpNm)));
            condMap.put("cond1", String.format("[검색어] : %s", StringUtils.defaultString( searchWord, "전체" )));
            condMap.put("cond2", String.format("[사용여부] : %s",
                    StringUtils.defaultString( codeService.getCodeNameByKey("C03", useAt), "전체" )
            ));

            String[] colHeaders = {"No", "회사코드", "회사", "코드", "고객사", "약식명", "대표자", "사업자번호", "이메일", "전화번호",
                    "우편번호", "주소", "담당자명", "담당자 이메일", "담당자 전화", "사용여부", "노출여부", "비고", "등록일시", "등록자"};

            return customerService.exportToExcelForCustomer(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "신규 추가 고객사 목록 조회", description = "신규 추가 고객사 목록 조회")
    @RequestMapping(value = "/helperForNew", method = {RequestMethod.GET})
    public ListResult<Partner> findCustomerForNew(
            @Parameter(description = "회사코드") @RequestParam(required = false) String corpCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord)
    {
        List<Partner> resultSet = null;
        try {
            resultSet = customerService.helperCustomerForNew(corpCd, searchWord, AppContant.CommonValue.YES.getValue());
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }


    @Operation(summary = "SAP 로부터 고객정보 적재", description = "SAP 로부터 고객정보 적재")
    @RequestMapping(value = "/downloadFromSAP", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> downloadFromSAP(
            @Parameter(description = "회사코드", required = true) @RequestParam String corpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int resultCount = customerService.downloadCustomerFromSap(corpCd, reqUserUid);
            Map<String, Object> result = new HashMap<>();
            result.put("successCount", Integer.valueOf(resultCount));
            return responseService.getDataResult(result);
        } catch (Exception e) {
            return responseService.getDataResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage(), null);
        }
    }




}
