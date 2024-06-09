package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.biz.code.dto.Corporation;
import com.guroomsoft.icms.biz.code.service.CorporationService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "회사 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/corporation")
public class CorporationController {
    private final ResponseService responseService;
    private final CorporationService corporationService;

    @Operation(summary = "회사 목록 조회", description = "회사 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Corporation> findCorporation(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "국가코드(C08)") @RequestParam(required = false) String corpCountry,
            @Parameter(description = "사용여부(C03)") @RequestParam(required = false, defaultValue = "Y") String useAt)
    {
        Corporation cond = new Corporation();
        if (StringUtils.isNotBlank(searchWord)) cond.setCorpNm(searchWord);
        if (StringUtils.isNotBlank(corpCountry)) cond.setCorpCountry(corpCountry);
        if (StringUtils.isNotBlank(useAt)) cond.setUseAt(useAt);

        List<Corporation> resultSet = null;
        try {
            resultSet = corporationService.findCorporation(cond);
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "회사 상세 조회", description = "회사 상세 조회")
    @RequestMapping(value = "/{corpCd}", method = {RequestMethod.GET})
    public DataResult<Corporation> findCorporationByKey(
            @Parameter(description = "회사 코드", required = true) @PathVariable String corpCd)
    {
        try {
            Corporation resultSet = corporationService.findCorporationByKey(corpCd);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "회사 등록 처리", description = "회사 등록 처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<Corporation> registration(
            @Parameter(description = "등록정보", required = true) @RequestBody Corporation data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            Corporation newData = corporationService.create(data, Long.valueOf(reqUserUid));
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

    @Operation(summary = "회사 수정 처리", description = "회사 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modify(
            @Parameter(description = "등록정보", required = true) @RequestBody Corporation data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int updated = corporationService.modify(data, Long.valueOf(reqUserUid));
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

    @Operation(summary = "회사 삭제 처리", description = "회사 삭제 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult remove(
            @Parameter(description = "회사 코드", required = true) @RequestParam String corpCd)
    {
        try {
            corporationService.remove(corpCd);
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CRemoveFailException e) {
            return responseService.getFailResult(CRemoveFailException.getCode(), CRemoveFailException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

}
