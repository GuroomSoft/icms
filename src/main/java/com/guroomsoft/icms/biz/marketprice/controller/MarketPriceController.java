package com.guroomsoft.icms.biz.marketprice.controller;

import com.guroomsoft.icms.biz.marketprice.dto.FerrousPrice;
import com.guroomsoft.icms.biz.marketprice.dto.NonferrousPrice;
import com.guroomsoft.icms.biz.marketprice.service.MarketPriceService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.CNotFoundException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "시세단가 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/marketprice")
public class MarketPriceController {
    private final ResponseService responseService;
    private final MarketPriceService marketPriceService;


    @Operation(summary = "비철금속 시세정보 가져오기", description = "비철금속 시세정보 가져오기")
    @RequestMapping(value = "/nonferrous/load", method = {RequestMethod.GET})
    public CommonResult loadNonferrousMetal()
    {
        try {
            int loadCount = marketPriceService.loadLMEMarketPrice();
            if (loadCount > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "비철금속 시세정보 조회", description = "비철금속 시세정보 조회")
    @RequestMapping(value = "/nonferrous/getLastPrice", method = {RequestMethod.GET})
    public ListResult<NonferrousPrice> findNonferrousMetal()
    {
        List<NonferrousPrice> resultSet = null;
        try {
            resultSet = marketPriceService.findLMEMarketPrice();
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "비철금속 시세정보 대시보드용 데이터 조회", description = "비철금속 시세정보 대시보드용 데이터 조회")
    @RequestMapping(value = "/nonferrous/getDashboardData", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getNonferrouseMetalData()
    {
        Map<String, Object> resultSet = null;
        try {
            resultSet = marketPriceService.getNonferrouseMetalData();
            return responseService.getDataResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "철강 시세정보 대시보드용 데이터 조회", description = "철강 시세정보 대시보드용 데이터 조회")
    @RequestMapping(value = "/ferrous/getDashboardData", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getFerrouseMetalData()
    {
        Map<String, Object> resultSet = null;
        try {
            resultSet = marketPriceService.getFerrousHistoryForDashboard();
            return responseService.getDataResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }



    @Operation(summary = "철강재 시세정보 가져오기", description = "철강재 시세정보 가져오기")
    @RequestMapping(value = "/ferrous/load", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> loadFerrousMetal()
    {
        try {
            Map<String, Object> resultSet = marketPriceService.loadMetalMarketPrice();
            return responseService.getDataResult(resultSet);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "철강재 시세정보 조회", description = "철강재 시세정보 조회")
    @RequestMapping(value = "/ferrous/getLastPrice", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> findFerrousMetal()
    {
        Map<String, Object> resultSet = new LinkedHashMap<>();
        try {
            String baseDate = marketPriceService.getFerrousLastDate();
            resultSet.put("baseDate", baseDate);

            List<FerrousPrice> data = marketPriceService.findFerrousPrice(baseDate);
            resultSet.put("data", data);
            return responseService.getDataResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }



}
