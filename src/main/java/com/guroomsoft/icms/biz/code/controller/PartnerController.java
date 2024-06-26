package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.biz.code.service.CodeService;
import com.guroomsoft.icms.biz.code.service.PartnerService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "협력사 마스터 및 협력사 담당 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/partner")
public class PartnerController {
    private final ResponseService responseService;
    private final PartnerService partnerService;
    private final CodeService codeService;

    @Operation(summary = "협력사 마스터 목록 조회", description = "협력사 마스터 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Partner> findPartner(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "사업자번호") @RequestParam(required = false) String bizRegNo,
            @Parameter(description = "대표자명") @RequestParam(required = false) String ceoNm,
            @Parameter(description = "사용여부") @RequestParam(required = false) String useAt)
    {
        Partner cond = new Partner();
        if (StringUtils.isNotBlank(searchWord)) cond.setBpNm(searchWord);
        if (StringUtils.isNotBlank(bizRegNo)) cond.setBizRegNo(bizRegNo);
        if (StringUtils.isNotBlank(ceoNm)) cond.setCeoNm(ceoNm);
        if (StringUtils.isNotBlank(useAt)) cond.setUseAt(useAt);

        List<Partner> resultSet = null;
        try {
            resultSet = partnerService.findPartner(cond);
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "코드 검색용 협력사 마스터 목록 조회", description = "코드 검색용 협력사 마스터 목록 조회")
    @RequestMapping(value = "/helper", method = {RequestMethod.GET})
    public ListResult<Partner> findPartnerHelper(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "담당자 UID") @RequestParam(required = false, defaultValue = "0") long userUid)
    {
        Map<String, Object> cond = new HashMap<>();
        if (StringUtils.isNotBlank(searchWord)) cond.put("searchWord", searchWord);
        if (userUid > 0) cond.put("userUid", Long.valueOf(userUid));

        List<Partner> resultSet = null;
        try {
            resultSet = partnerService.findBpHelper(cond);
            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "협력사 마스터 상세 조회", description = "협력사 마스터 상세 조회")
    @RequestMapping(value = "/{bpCd}", method = {RequestMethod.GET})
    public DataResult<Partner> findPartnerByKey(
            @Parameter(description = "BP 코드", required = true) @PathVariable String bpCd)
    {
        try {
            Partner resultSet = partnerService.findPartnerByKey(bpCd);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "협력업체 등록처리", description = "협력업체 등록처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<Partner> registrationPartner(
            @Parameter(description = "등록정보", required = true) @RequestBody Partner data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int inserted = partnerService.createPartner(data, Long.valueOf(reqUserUid));
            if (inserted > 0) {
                Partner newData = partnerService.findPartnerByKey(data.getBpCd());
                return responseService.getDataResult(newData);
            } else {
                return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
            }
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult(CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(), null);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CRegistrationFailException e) {
            return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "협력업체 수정 처리", description = "협력업체 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifyPartner(
            @Parameter(description = "등록정보", required = true) @RequestBody Partner data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int updated = partnerService.modifyPartner(data, Long.valueOf(reqUserUid));
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

    @Operation(summary = "협력사 마스터 엑셀 업로드 등록처리", description = "멀티 협력사 정보를 병합처리")
    @RequestMapping(value = "/registration/multiple", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> registrationMultiPartner(
            @Parameter(description = "등록정보", required = true) @RequestBody List<Partner> dataList,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Map<String, Object> resultMap = partnerService.saveMultiplePartner(dataList, Long.valueOf(reqUserUid));
        return responseService.getDataResult(resultMap);
    }


    @Operation(summary = "협력사 마스터 미사용 처리", description = "협력사 마스터 미사용 처리, 공급사 및 고객사 참조정보도 업데이트 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removePartner(
            @Parameter(description = "BP 코드", required = true) @RequestParam String bpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = partnerService.disablePartnerStatus(bpCd, reqUserUid);
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (CRemoveFailException e) {
            return responseService.getDataResult(CRemoveFailException.getCode(), CRemoveFailException.getCustomMessage(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "협력사 마스터 대장 엑셀 내보내기", description = "협력사 마스터 대장 엑셀 내보내기")
    @RequestMapping(value = "/exportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelForBp(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "사용여부") @RequestParam(required = false) String useAt)
    {
        Partner cond = new Partner();
        if (StringUtils.isNotBlank(searchWord)) { cond.setBpNm(searchWord);}
        if (StringUtils.isNotBlank(useAt)) { cond.setUseAt(useAt); }

        try {
            List<Partner> resultList = partnerService.findPartner(cond);
            String reportTitle = "거래처대장";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("cond0", String.format("[검색어] : %s", StringUtils.defaultString( searchWord, "전체" )));
            condMap.put("cond1", String.format("[사용여부] : %s",
                    StringUtils.defaultString( codeService.getCodeNameByKey("C03", useAt), "전체" )
            ));

            String[] colHeaders = {"No", "코드", "고객사", "약식명", "대표자", "사업자번호", "이메일", "전화번호",
                    "우편번호", "주소", "담당자명", "담당자 이메일", "담당자 전화", "사용여부", "노출여부", "비고", "등록일시", "등록자"};

            return partnerService.exportToExcelForBp(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultList);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(summary = "담당 협력업체 등록 처리", description = "담당 협력업체 등록 처리")
    @RequestMapping(value = "/assigned", method = {RequestMethod.POST})
    public CommonResult registrationEmpBp(
            @Parameter(description = "사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(description = "BP 코드", required = true) @RequestParam String bpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Partner data = new Partner();
        data.setUserUid(userUid);
        data.setBpCd(bpCd);
        data.setRegUid(reqUserUid);

        try {
            int inserted = partnerService.createEmpBp(data);
            if (inserted > 0) {
                return responseService.getSuccessResult();
            }

            return responseService.getFailResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "담당 협력업체 삭제 처리", description = "담당 협력업체 삭제 처리")
    @RequestMapping(value = "/assigned/release", method = {RequestMethod.POST})
    public CommonResult removeEmpBp(
            @Parameter(description = "사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(description = "BP 코드", required = true) @RequestParam String bpCd)
    {
        Partner data = new Partner();
        data.setUserUid(userUid);
        data.setBpCd(bpCd);
        try {
            int inserted = partnerService.removeEmpBp(data);
            if (inserted > 0) {
                return responseService.getSuccessResult();
            }

            return responseService.getFailResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "담당 협력업체 멀티 등록 처리", description = "담당 협력업체 멀티 등록 처리")
    @RequestMapping(value = "/assigned/multiple", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> registrationEmpMultiBp(
            @Parameter(description = "요청정보", required = true) @RequestBody Map<String, Object> param,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Long userUid = null;
        List<String> bpList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();

        if (param.containsKey("userUid"))
            userUid = Long.valueOf(((Integer)param.get("userUid")).longValue());

        if (param.containsKey("bpList"))
            bpList = (List)param.get("bpList");

        try {
            int inserted = partnerService.createEmpMultiBp(userUid, bpList, reqUserUid);
            resultMap.put("successCount", Integer.valueOf(inserted));
            resultMap.put("failCount", Integer.valueOf(bpList.size() - inserted));
            return responseService.getDataResult(resultMap);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "담당 협력업체 멀티 삭제 처리", description = "담당 협력업체 멀티 삭제 처리")
    @RequestMapping(value = "/assigned/release/multiple", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> removeEmpMultiBp(
            /*@Parameter(description = "사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(description = "BP 코드", required = true) @RequestBody List<String> bpList*/
            @Parameter(description = "요청정보", required = true) @RequestBody Map<String, Object> param,
            @Parameter(hidden = true) @RequestParam long reqUserUid
    )

    {
        Map<String, Object> resultMap = new HashMap<>();

        Long userUid = null;
        List<String> bpList = new ArrayList<>();
        if (param.containsKey("userUid"))
            userUid = Long.valueOf(((Integer)param.get("userUid")).longValue());

        if (param.containsKey("bpList"))
            bpList = (List)param.get("bpList");

        try {
            int deleted = partnerService.removeEmpMultiBp(userUid, bpList);
            resultMap.put("successCount", Integer.valueOf(deleted));
            resultMap.put("failCount", Integer.valueOf(bpList.size() - deleted));
            return responseService.getDataResult(resultMap);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "담당 협력사 목록 조회", description = "담당 협력사 목록 조회")
    @RequestMapping(value = "/assignedPartner", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> findPartnerForEmployee(
            @Parameter(description = "담당 사용자 UID", required = true) @RequestParam Long userUid)
    {
        List<Map<String, Object>> resultSet = new ArrayList<>();
        try {
            resultSet = partnerService.findPartnerForEmployee(userUid);
            return responseService.getListResult(resultSet);
        } catch (CInvalidArgumentException e) {
            throw new CInvalidArgumentException();
        } catch (CUnknownException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "담당 외 협력사 목록 조회", description = "담당 외 협력사 목록 조회")
    @RequestMapping(value = "/notAssignedPartner", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> findPartnerForOthers(
            @Parameter(description = "담당 사용자 UID", required = true) @RequestParam Long userUid,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "협력사구분") @RequestParam(required = false) String bpType,
            @Parameter(description = "상태구분") @RequestParam(required = false) String useAt)
    {
        Map<String, Object> cond = new HashMap<>();
        cond.put("userUid", userUid);
        if (StringUtils.isNotBlank(searchWord)) cond.put("searchWord", searchWord);
        if (StringUtils.isNotBlank(bpType)) cond.put("bpType", bpType);
        if (StringUtils.isNotBlank(useAt)) cond.put("useAt", useAt);

        List<Map<String, Object>> resultSet = new ArrayList<>();
        try {
            resultSet = partnerService.findPartnerForOthers(cond);
            return responseService.getListResult(resultSet);
        } catch (CInvalidArgumentException e) {
            throw new CInvalidArgumentException();
        } catch (CUnknownException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }


}
