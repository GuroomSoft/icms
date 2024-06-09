package com.guroomsoft.icms.biz.material.controller;

import com.guroomsoft.icms.biz.code.service.CodeService;
import com.guroomsoft.icms.biz.material.dto.Material;
import com.guroomsoft.icms.biz.material.service.MaterialService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "재질마스터 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/material")
public class MaterialController {
    private final ResponseService responseService;
    private final MaterialService materialService;
    private final CodeService codeService;

    @Operation(summary = "재질마스터 목록 조회", description = "재질마스터 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Material> findMaterial(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "재질구분(B01)") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "유형(강종)") @RequestParam(required = false) String steelGrade,
            @Parameter(description = "고객사재질코드") @RequestParam(required = false) String customerMatCd)
    {
        Material cond = new Material();
        if (StringUtils.isNotBlank(searchWord)) cond.setMaterialNm(searchWord);
        if (StringUtils.isNotBlank(rawMaterialCd)) cond.setRawMaterialCd(rawMaterialCd);
        if (StringUtils.isNotBlank(steelGrade)) cond.setSteelGrade(steelGrade);
        if (StringUtils.isNotBlank(customerMatCd)) cond.setCustomerMatCd(customerMatCd);

        List<Material> resultSet = null;
        try {
            resultSet = materialService.findMaterial(cond);
            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CUnknownException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "재질마스터 엑셀 내보내기", description = "재질마스터 엑셀 내보내기")
    @RequestMapping(value = "/exportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelManagementSheetForMaterial(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "재질구분(B01)") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "유형(강종)") @RequestParam(required = false) String steelGrade,
            @Parameter(description = "고객사재질코드") @RequestParam(required = false) String customerMatCd)
    {
        Material cond = new Material();
        if (StringUtils.isNotBlank(searchWord)) cond.setMaterialNm(searchWord);
        if (StringUtils.isNotBlank(rawMaterialCd)) cond.setRawMaterialCd(rawMaterialCd);
        if (StringUtils.isNotBlank(steelGrade)) cond.setSteelGrade(steelGrade);
        if (StringUtils.isNotBlank(customerMatCd)) cond.setCustomerMatCd(customerMatCd);

        try {
            List<Material> resultSet = materialService.findMaterial(cond);
            String reportTitle = "재질마스터";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            if (StringUtils.isBlank(searchWord)) {
                condMap.put("cond0", String.format("[검색어] : %s", "전체"));
            } else {
                condMap.put("cond0", String.format("[검색어] : %s", StringUtils.defaultString( searchWord, "전체" )));
            }

            if (StringUtils.isNotBlank(rawMaterialCd)) {
                condMap.put("cond1", String.format("[원소재] : %s",  codeService.getCodeNameByKey( "B01", rawMaterialCd )));
            } else {
                condMap.put("cond1", String.format("[원소재] : %s", "전체"));
            }

            if ( StringUtils.isBlank(steelGrade) ) {
                condMap.put("cond2", String.format("[강종] : %s", "전체" ));
            } else {
                condMap.put("cond2", String.format("[강종] : %s", StringUtils.defaultString(steelGrade, "전체") ));
            }

            if ( StringUtils.isBlank(customerMatCd) ) {
                condMap.put("cond3", String.format("[고객사 재질코드] : %s", "전체" ));
            } else {
                condMap.put("cond3", String.format("[고객사 재질코드] : %s", StringUtils.defaultString(customerMatCd, "전체") ));
            }


            String[] colHeaders = {"No", "원소재코드", "원소재명", "유형(강종)", "재질코드", "재질명", "고객사재질코드", "비고", "사용여부", "등록일시", "등록자"};

            return materialService.exportToExcelForMaterial(
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

    @Operation(summary = "재질마스터 상세 조회", description = "재질마스터 상세 조회")
    @RequestMapping(value = "/{materialCd}/{materialNm}", method = {RequestMethod.GET})
    public DataResult<Material> findMaterialByKey(
            @Parameter(description = "재질코드", required = true) @PathVariable String materialCd,
            @Parameter(description = "재질명", required = true) @PathVariable String materialNm)
    {
        try {
            Material resultSet = materialService.findMaterialByKey(materialCd, materialNm);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CDatabaseException e) {
            return responseService.getDataResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "재질마스터 등록 처리", description = "재질마스터 등록 처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<Material> registrationMaterial(
            @Parameter(description = "등록정보", required = true) @RequestBody Material data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            Material newData = materialService.createMaterial(data, Long.valueOf(reqUserUid));
            if (newData != null) {
                return responseService.getDataResult(newData);
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult(CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(), null);
        } catch (CRegistrationFailException e) {
            return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
    }

    @Operation(summary = "재질마스터 일괄 등록 처리", description = "재질마스터 일괄 등록 처리")
    @RequestMapping(value = "/registration/multiple", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> registrationMultipleMaterial(
            @Parameter(description = "등록정보", required = true) @RequestBody List<Material> dataList,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Map<String, Object> resultSet = new HashMap<>();

        try {
            int processCount = materialService.saveMultipleMaterial(dataList, Long.valueOf(reqUserUid));
            Integer successCount = Integer.valueOf(processCount);
            Integer failCount = Integer.valueOf(dataList.size() - processCount );
            resultSet.put("successCount", successCount);
            resultSet.put("failCount", failCount);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }


    @Operation(summary = "재질마스터 수정 처리", description = "재질마스터 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifyMaterial(
            @Parameter(description = "등록정보", required = true) @RequestBody Material data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int updated = materialService.modifyMaterial(data, Long.valueOf(reqUserUid));
            if (updated > 0) {
                return responseService.getSuccessResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CModifictionFailException e) {
            return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }

        return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
    }

    @Operation(summary = "재질마스터 삭제 처리", description = "재질마스터 삭제 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removePlant(
            @Parameter(description = "재질코드", required = true) @RequestParam String materialCd,
            @Parameter(description = "재질명", required = true) @RequestParam String materialNm)
    {
        try {
            int updated = materialService.removeMaterial(materialCd, materialNm);
            if (updated > 0) {
                return responseService.getSuccessResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CRemoveFailException e) {
            return responseService.getFailResult(CRemoveFailException.getCode(), CRemoveFailException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }

        return responseService.getFailResult(CRemoveFailException.getCode(), CRemoveFailException.getCustomMessage());
    }

}
