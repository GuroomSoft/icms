package com.guroomsoft.icms.common.controller;


import com.guroomsoft.icms.common.dto.AppConfig;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.AppConfigService;
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
@Tag(name = "시스템 환경설정 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sysconfig")
public class AppConfigController {
    private final ResponseService responseService;
    private final AppConfigService appConfigService;

    @Operation(summary = "시스템 환경설정보 목록 조회", description = "어플리케이션 구성설정 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<AppConfig> findList(
            @Parameter(description = "키") @RequestParam(name="envKey", required = false) String envKey,
            @Parameter(description = "설정값") @RequestParam(name="envValue", required = false) String envValue,
            @Parameter(description = "분류항목") @RequestParam(name="category", required = false) String category)
    {
        AppConfig cond = new AppConfig();
        if (StringUtils.isNotBlank(envKey)) cond.setEnvKey(envKey);
        if (StringUtils.isNotBlank(envValue)) cond.setEnvValue(envValue);
        if (StringUtils.isNotBlank(category)) cond.setCategory(category);

        List<AppConfig> resultSet = appConfigService.findAppConfig(cond);
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "시스템 환경설정보 저장", description = "어플리케이션 구성설정 정보 저장")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public CommonResult registrationAppConfig(
            @Parameter(description = "시스템 환경설정 정보", required = true) @RequestBody AppConfig data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setRegUid(Long.valueOf(reqUserUid));
            AppConfig updated = appConfigService.saveAppConfig(data);
            if (updated != null) {
                return responseService.getSuccessResult();
            }
        } catch (RuntimeException e) {
            if (e instanceof CRequiredException) {
                return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage());
            } else if (e instanceof CBizProcessFailException) {
                return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
            } else {
                return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
            }
        } catch (Exception e) {
            log.error(this.getClass().getName() + " ::: {}", e.getMessage());
        }

        return responseService.getFailResult();
    }

    @Transactional
    @Operation(summary = "시스템 환경설정보 일괄 저장", description = "어플리케이션 구성설정 정보 일괄 저장")
    @RequestMapping(value = "/registration/all", method = {RequestMethod.POST})
    public CommonResult registrationAppConfig(
            @Parameter(description = "시스템 환경설정 정보", required = true) @RequestBody List<AppConfig> dataList,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int saveCount = 0;
            for (AppConfig data : dataList) {
                data.setRegUid(Long.valueOf(reqUserUid));
                AppConfig updated = appConfigService.saveAppConfig(data);
                if (updated != null) {
                    saveCount++;
                }
            }

            if (saveCount == dataList.size()) {
                return responseService.getFailResult();
            } else {
                return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
            }
        } catch (RuntimeException e) {
            if (e instanceof CRequiredException) {
                return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage());
            } else if (e instanceof CBizProcessFailException) {
                return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
            } else {
                return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
            }
        } catch (Exception e) {
            log.error(this.getClass().getName() + " ::: {}", e.getMessage());
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

}
