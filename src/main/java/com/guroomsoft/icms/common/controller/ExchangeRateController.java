package com.guroomsoft.icms.common.controller;


import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ExchangeRate;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.ExchangeRateService;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "환율 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exchangerate")
public class ExchangeRateController {
    private final ResponseService responseService;
    private final ExchangeRateService exchangeRateService;

    @Operation(summary = "환율 목록 조회", description = "환율 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<ExchangeRate> findList(
            @Parameter(description = "기준일") @RequestParam(name="searchDate") String searchDate)
    {
        List<ExchangeRate> resultList = null;
        try {
            resultList = exchangeRateService.findExchangeRate(searchDate);
            return responseService.getListResult(resultList);
        } catch (CBizProcessFailException e) {
            throw new CBizProcessFailException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "Reload 환율", description = "Reload 환율")
    @RequestMapping(value = "/reload", method = {RequestMethod.GET})
    public ListResult<ExchangeRate> fetchExchangeRate(
            @Parameter(description = "기준일") @RequestParam(name="searchDate") String searchDate)
    {
        List<ExchangeRate> resultList = null;
        try {
            resultList = exchangeRateService.fetchExchangeRate(searchDate);
            exchangeRateService.loadExchangeRate(resultList);
            return responseService.getListResult(resultList);
        } catch (CBizProcessFailException e) {
            throw new CBizProcessFailException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }


    @Operation(summary = "최종 환율 영업일 조회", description = "최종 환율 영업일 조회")
    @RequestMapping(value = "/getExchangeRateDate", method = {RequestMethod.GET})
    public DataResult<Map<String, String>> getExchangeRateDate()
    {
        Map<String, String> resultMap = new HashMap<>();
        String stdDate = exchangeRateService.getLastExchangeRateDate();
        resultMap.put("stdDate", stdDate);
        return responseService.getDataResult(resultMap);
    }

    @Operation(summary = "환율단위 목록 조회", description = "환율단위 목록 조회")
    @RequestMapping(value = "/getExchangeUnit", method = {RequestMethod.GET})
    public ListResult<Map<String, String>> getExchangeRateUnit()
    {
        List<Map<String, String>> resultSet = new ArrayList<>();
        resultSet = exchangeRateService.findExchangeUnit();
        return responseService.getListResult(resultSet);
    }


    @Operation(summary = "대시보드용 환율 이력조회", description = "대시보드용 환율 이력조회")
    @RequestMapping(value = "/getExchangeHistory", method = {RequestMethod.GET})
    public ListResult<Map<String, Object>> getExchangeHistory()
    {
        List<Map<String, Object>> resultSet = null;
        try {
            resultSet = exchangeRateService.findBaseRateForLastMonth();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
        return responseService.getListResult(resultSet);
    }

}
