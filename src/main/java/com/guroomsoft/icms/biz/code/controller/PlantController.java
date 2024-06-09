package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.biz.code.service.PlantService;
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
@Tag(name = "플랜트 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plant")
public class PlantController {
    private final ResponseService responseService;
    private final PlantService plantService;

    @Operation(summary = "Plant 목록 조회", description = "Plant 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Plant> findPlant(
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "국가코드(C08)") @RequestParam(required = false) String plantCountry,
            @Parameter(description = "회사코드") @RequestParam(required = false) String corpCd,
            @Parameter(description = "사용여부(C03)") @RequestParam(required = false, defaultValue = "Y") String useAt)
    {
        Plant cond = new Plant();
        if (StringUtils.isNotBlank(searchWord)) cond.setPlantNm(searchWord);
        if (StringUtils.isNotBlank(plantCountry)) cond.setPlantCountry(plantCountry);
        if (StringUtils.isNotBlank(corpCd)) cond.setCorpCd(corpCd);
        if (StringUtils.isNotBlank(useAt)) cond.setUseAt(useAt);

        List<Plant> resultSet = null;
        try {
            resultSet = plantService.findPlant(cond);
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "플랜트 상세 조회", description = "플랜트 상세 조회")
    @RequestMapping(value = "/{plantCd}", method = {RequestMethod.GET})
    public DataResult<Plant> findPlantByKey(
            @Parameter(description = "플랜트 코드", required = true) @PathVariable String plantCd)
    {
        try {
            Plant resultSet = plantService.findPlantByKey(plantCd);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "플랜트 등록 처리", description = "플랜트 등록 처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<Plant> registrationPlant(
            @Parameter(description = "등록정보", required = true) @RequestBody Plant data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        data.setRegUid(reqUserUid);
        try {
            Plant newData = plantService.createPlant(data);
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

    @Operation(summary = "플랜트 수정 처리", description = "플랜트 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifyPlant(
            @Parameter(description = "등록정보", required = true) @RequestBody Plant data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        data.setModUid(reqUserUid);
        try {
            int updated = plantService.modifyPlant(data);
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

    @Operation(summary = "플랜트 삭제 처리", description = "플랜트 삭제 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removePlant(
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd)
    {
        try {
            plantService.removePlant(plantCd);
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
