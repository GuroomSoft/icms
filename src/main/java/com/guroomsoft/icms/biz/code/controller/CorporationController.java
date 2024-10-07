package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.auth.dto.Group;
import com.guroomsoft.icms.biz.code.dto.Corporation;
import com.guroomsoft.icms.biz.code.dto.GroupPlant;
import com.guroomsoft.icms.biz.code.dto.Plant;
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
import org.springframework.transaction.annotation.Transactional;
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

    /** 플랜트 그룹 관련 */
    @Operation(summary = "플랜트 그룹 목록 조회", description = "플랜트 그룹 목록 조회")
    @RequestMapping(value = "/group", method = {RequestMethod.POST})
    public ListResult<Group> findGroups(
            @Parameter(description = "조회조건") @RequestBody Group cond)
    {
        try {
            List<Group> groups = corporationService.findPlantGroups(cond);
            return responseService.getListResult(groups);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "플랜트 그룹 조회(단건)", description = "플랜트 그룹 조회(단건)")
    @RequestMapping(value = "/group/{grpUid}", method = {RequestMethod.GET})
    public DataResult<Group> findGroups(
            @Parameter(description = "조회조건") @PathVariable long grpUid)
    {
        try {
            Group group = corporationService.findPlantGroupByKey(Long.valueOf(grpUid));
            return responseService.getDataResult(group);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "플랜트 그룹 등록", description = "플랜트 그룹 등록 후 그룹 권한의 자동 등록 처리됨")
    @RequestMapping(value = "/group/registration", method = {RequestMethod.POST})
    public DataResult<Group> registrationGroup(
            @Parameter(description = "그룹정보", required = true) @RequestBody Group group,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            group.setRegUid(Long.valueOf(reqUserUid));
            Group newItem = corporationService.createGroups(group);
            return responseService.getDataResult(newItem);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "플랜트 그룹 수정", description = "플랜트 그룹 수정 처리")
    @RequestMapping(value = "/group/modify", method = {RequestMethod.POST})
    public DataResult<Group> modifyGroup(
            @Parameter(description = "그룹정보", required = true) @RequestBody Group group,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            Group updatedItem = corporationService.modifyGroups(group);
            return responseService.getDataResult(updatedItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "플랜트 그룹 삭제", description = "플랜트 그룹 삭제처리 - 그룹멤버 삭제, 그룹 권한 삭제")
    @RequestMapping(value = "/group/remove", method = {RequestMethod.POST})
    public CommonResult removeGroup(
            @Parameter(description = "그룹정보", required = true) @RequestParam long grpUid,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = corporationService.removeGroups(Long.valueOf(grpUid));
            if (deleted == 0) {
                return responseService.getFailResult();
            }
            return responseService.getSuccessResult();
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (CBizProcessFailException e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }
    /* --------------------------------------------------------------------------------------------------------- */
    /** 그룹 멤버 */
    @Operation(summary = "플랜트 그룹 멤버 조회", description = "사용자 그룹 멤버 목록 조회 - 필수 파라미터 grpUid")
    @RequestMapping(value = "/group/item/{grpUid}", method = {RequestMethod.POST})
    public ListResult<GroupPlant> findPlantGroupItems(
            @Parameter(description = "그룹 UID", required = true) @PathVariable Long grpUid)
    {
        try {
            List<GroupPlant> items = corporationService.findPlantGroupItem(grpUid);
            return responseService.getListResult(items);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "플랜트 그룹 멤버 이외 조회", description = "사용자 그룹 멤버 목록 조회 - 파라미터 X")
    @RequestMapping(value = "/group/item/findPlantGroupOuterItems", method = {RequestMethod.POST})
    public ListResult<Plant> findPlantGroupOuterItems(
            //@Parameter(description = "플랜트 코드", required = true) @PathVariable String plantCd
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            List<Plant> items = corporationService.findPlantGroupOuterItem();
            return responseService.getListResult(items);
        } catch (Exception e) {
            return responseService.getListResult(null);
        }
    }

    @Operation(summary = "플랜트 그룹 멤버 등록", description = "플랜트 그룹 멤버 단건 등록")
    @RequestMapping(value = "/group/item/registration", method = {RequestMethod.POST})
    public DataResult<GroupPlant> registrationGroupPlant(
            @Parameter(description = "그룹 UID", required = true) @RequestParam Long grpUid,
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            GroupPlant data = new GroupPlant();
            data.setPlantCd(plantCd);
            corporationService.removePlantGroupItem(data);    // 이전 그룹 정보 삭제

            data.setGrpUid(grpUid);
            data.setRegUid(Long.valueOf(reqUserUid));
            GroupPlant newItem = corporationService.createPlantGroupItem(data);
            return responseService.getDataResult(newItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "플랜트 그룹 멤버 일괄 등록", description = "플랜트 그룹에 등록한 PlantCd를 멀티로 전송하여 한번에 등록처리")
    @RequestMapping(value = "/group/item/registration/batch/{grpUid}", method = {RequestMethod.POST})
    @Transactional
    public CommonResult registrationMultiGroupPlant(
            @Parameter(description = "그룹 UID", required = true) @PathVariable Long grpUid,
            @Parameter(description = "플랜트 코드", required = true) @RequestBody List<String> newPlantCds,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            if ( (newPlantCds == null) || (newPlantCds.isEmpty()) ) {
                return responseService.getFailResult();
            }
            int inserted = 0;

            for(String plantCd : newPlantCds) {
                GroupPlant data = new GroupPlant();
                data.setPlantCd(plantCd);
                corporationService.removePlantGroupItem(data);    // 이전 그룹 정보 삭제

                data.setGrpUid(grpUid);
                data.setRegUid(Long.valueOf(reqUserUid));
                GroupPlant newItem = corporationService.createPlantGroupItem(data);
                if (newItem != null) inserted++;
            }

            if (inserted == newPlantCds.size()) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult(CBizProcessFailException.getCode(), String.format("요청건수 : %d 건, 등록건수 : %d", newPlantCds.size(), inserted));
            }
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult( CInvalidArgumentException.getCode(), e.getMessage(), null);

        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "플랜트 그룹 멤버 수정", description = "ORD 수정")
    @RequestMapping(value = "/group/item/modify/batch/{grpUid}", method = {RequestMethod.POST})
    @Transactional
    public CommonResult modifyGroupPlant(
            @Parameter(description = "그룹정보", required = true) @RequestBody List<GroupPlant> conn,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            if ( (conn == null) || (conn.isEmpty()) ) {
                return responseService.getFailResult();
            }
            int totalUpdate = 0;

            for(GroupPlant data : conn) {
                totalUpdate = totalUpdate + corporationService.modifyGroupItem(data, reqUserUid);
            }

            if (totalUpdate == conn.size()) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult(CBizProcessFailException.getCode(), String.format("요청건수 : %d 건, 수정건수 : %d", conn.size(), totalUpdate));
            }

            //return responseService.getDataResult(updatedItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "플랜트 그룹 멤버 멀티 삭제", description = "플랜트 그룹 멤버 일괄 삭제처리")
    @RequestMapping(value = "/group/item/remove/batch/{grpUid}", method = {RequestMethod.POST})
    public CommonResult removeGroupPlant(
            @Parameter(description = "그룹 UID", required = true) @PathVariable Long grpUid,
            @Parameter(description = "플랜트 코드", required = true) @RequestBody List<String> targetPlants)
    {
        try {
            if ( (targetPlants == null) || (targetPlants.isEmpty()) ) {
                return responseService.getFailResult();
            }
            int totalDeleted = 0;

            for(String userUid : targetPlants) {
                GroupPlant cond = new GroupPlant();
                cond.setGrpUid(grpUid);
                cond.setPlantCd(userUid);
                totalDeleted = totalDeleted + corporationService.removePlantGroupItem(cond);
            }

            if (totalDeleted == targetPlants.size()) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult(CBizProcessFailException.getCode(), String.format("요청건수 : %d 건, 삭제건수 : %d", targetPlants.size(), totalDeleted));
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

}
