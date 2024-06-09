package com.guroomsoft.icms.common.controller;


import com.guroomsoft.icms.auth.dto.GroupAuthority;
import com.guroomsoft.icms.common.dto.*;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.common.service.MenuService;
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
@Tag(name = "시스템 메뉴 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/menu")
public class MenuController {
    private final ResponseService responseService;
    private final MenuService menuService;

    @Operation(summary = "시스템 메뉴 목록 조회", description = "시스템 메뉴를 계층적인 형식으로 반환")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<Menu> findMenuList()
    {
        List<Menu> resultSet = null;
        try {
            resultSet = menuService.findAllMenus(Long.valueOf(0));
        } catch (Exception e) {
            log.error("{} :: findMenuList", this.getClass().getSimpleName());
        }
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "시스템 메뉴 목록 조회", description = "시스템 메뉴 목록을 단순 리스트 형식으로 반환")
    @RequestMapping(value = "/findMenus/{userUid}", method = {RequestMethod.GET})
    public ListResult<GroupAuthority> findMenus(
            @Parameter(description = "사용자 UID", required = true) @PathVariable Long userUid)
    {
        List<GroupAuthority> resultSet = null;
        try {
            resultSet = menuService.findAuthorizedMenusByPlain(userUid, null);
        } catch (Exception e) {
            log.error("{} :: findMenuList", this.getClass().getSimpleName());
        }
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "시스템 메뉴 등록", description = "시스템 메뉴 등록처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<Menu> registrationMenu(
            @Parameter(description = "메뉴정보", required = true) @RequestBody Menu data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setRegUid(Long.valueOf(reqUserUid));
            Menu newItem = menuService.createMenu(data);
            return responseService.getDataResult(newItem);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult( CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "시스템 메뉴 정보 수정", description = "시스템 정보 수정처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public DataResult<Menu> modifyMenu(
            @Parameter(description = "메뉴정보", required = true) @RequestBody Menu data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setModUid(Long.valueOf(reqUserUid));
            Menu newItem = menuService.modifyMenu(data);
            return responseService.getDataResult(newItem);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "메뉴 삭제 처리", description = "메뉴 삭제, 권한 삭제")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removeMenu(
            @Parameter(description = "메뉴정보", required = true) @RequestBody Menu data)
    {
        try {
            int deleted = menuService.removeMenu(data.getMenuUid());
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult( CRequiredException.getCode(), CRequiredException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }


}
