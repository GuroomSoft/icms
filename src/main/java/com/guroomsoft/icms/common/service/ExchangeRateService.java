package com.guroomsoft.icms.common.service;

import com.guroomsoft.icms.common.dao.ExchangeRateDAO;
import com.guroomsoft.icms.common.dto.ExchangeRate;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.DateTimeUtil;
import com.guroomsoft.icms.util.ExchangeRateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO;

    public List<ExchangeRate> fetchExchangeRate(String searchDate) {
        List<ExchangeRate> resultList = new ArrayList<>();
        String standardDate = "";
        if (StringUtils.isBlank(searchDate)) {
            standardDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        } else {
            standardDate = StringUtils.replace(searchDate, "-", "");
            standardDate = StringUtils.replace(standardDate, ".", "");
        }

        JSONArray resultArray = ExchangeRateUtil.getExchangeRateUtil(standardDate);
        if (resultArray == null) {
            return null;
        }

        for (Object o : resultArray) {
            JSONObject exchangeRateInfo = (JSONObject) o;
            if ( Integer.valueOf(exchangeRateInfo.get("result").toString()).intValue() != 1) {
                continue;
            }

            ExchangeRate er = new ExchangeRate();
            er.setStdDate(standardDate);
            er.setCurUnit(StringUtils.defaultString(exchangeRateInfo.get("cur_unit").toString()));
            er.setCurNm(StringUtils.defaultString(exchangeRateInfo.get("cur_nm").toString()));
            er.setTtb(StringUtils.defaultString(exchangeRateInfo.get("ttb").toString()));
            er.setTts(StringUtils.defaultString(exchangeRateInfo.get("tts").toString()));
            er.setDealBasR(StringUtils.defaultString(exchangeRateInfo.get("deal_bas_r").toString()));
            er.setBkpr(StringUtils.defaultString(exchangeRateInfo.get("bkpr").toString()));
            er.setYyEfeeR(StringUtils.defaultString(exchangeRateInfo.get("yy_efee_r").toString()));
            er.setTenDdEfeeR(StringUtils.defaultString(exchangeRateInfo.get("ten_dd_efee_r").toString()));
            er.setKftcDealBasR(StringUtils.defaultString(exchangeRateInfo.get("kftc_deal_bas_r").toString()));
            er.setKftcBkpr(StringUtils.defaultString(exchangeRateInfo.get("kftc_bkpr").toString()));
            er.setRegUid(Long.valueOf(1));
            resultList.add(er);
        }

        return resultList;
    }

    @Transactional
    public int loadExchangeRate(List<ExchangeRate> exchangeList) throws Exception
    {
        if ( exchangeList == null || exchangeList.isEmpty() ) {
            return 0;
        }
        int totalCount =0;
        try {
            for (ExchangeRate item : exchangeList)
            {
                removeExchangeRate(item.getStdDate(), item.getCurUnit());
                int updated = createExchangeRate(item);
                if (updated > 0) {
                    totalCount++;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return totalCount;
    }

    public List<ExchangeRate> findExchangeRate(String stdDate) throws Exception
    {
        List<ExchangeRate> resultSet = null;
        try {
            resultSet = exchangeRateDAO.selectExchangeRate(stdDate);
            if (resultSet == null || resultSet.isEmpty()) {
                resultSet = fetchExchangeRate(stdDate);
                if (resultSet != null && !resultSet.isEmpty()) {
                    loadExchangeRate(resultSet);
                }
            }

            return resultSet;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Transactional
    public int createExchangeRate(ExchangeRate data) throws Exception
    {
        try {
            int processCount = exchangeRateDAO.insertExchangeRate(data);

            return processCount;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    public int removeExchangeRate(String stdDate, String curUnit) throws Exception
    {
        try {
            int processCount = exchangeRateDAO.deleteExchangeRate(stdDate, curUnit);

            return processCount;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Transactional
    public int saveExchangeRateDate(String stdDate)
    {
        return exchangeRateDAO.saveExchangeRateDate(stdDate);
    }

    public String getLastExchangeRateDate()
    {
        return exchangeRateDAO.getLastExchangeRateDate();
    }

    public List<Map<String, String>> findExchangeUnit()
    {
        try {
            return exchangeRateDAO.selectExchangeUnit(AppContant.CommonValue.YES.getValue());
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findBaseRateForLastMonth() throws Exception
    {
        String colorList[] = {"hsl(231, 70%, 50%)", "hsl(32, 70%, 50%)",  "hsl(41, 70%, 50%)",  "hsl(325, 70%, 50%)",  "hsl(332, 70%, 50%)"};

        List<Map<String, Object>> resultSet = new ArrayList<>();
        List<Map<String, String>> curList = findExchangeUnit();

        if (curList != null && curList.size() > 0)
        {
            int idx = 0;
            for(Map<String, String> item : curList)
            {
                if (item.get("currencyUnit").equalsIgnoreCase("KRW")) {
                    continue;
                }
                List<Map<String, Object>> dataList = exchangeRateDAO.selectBaseRateForLastMonth(item.get("currencyUnit"));
                if (dataList == null || dataList.isEmpty()) {
                    continue;
                }
                Map<String, Object> value = new HashMap<String, Object>();
                value.put("id", item.get("currencyUnit"));
                value.put("color", colorList[idx % 5]);
                value.put("data", dataList);
                resultSet.add(value);

                idx++;
            }
        }

        return resultSet;
    }

    /*
        11시 5분에 실행
     */
    @Transactional
    @Scheduled(cron = "0 5 11 * * *")
    public void scheduleExchagerate()
    {
        String stdDate = StringUtils.replace(DateTimeUtil.currentDate(), "-", "");
        List<ExchangeRate> dataList = fetchExchangeRate(stdDate);
        if (dataList != null && dataList.size() > 0) {
            try {
                int updatedCount = loadExchangeRate(dataList);
                if (updatedCount >0) {
                    exchangeRateDAO.saveExchangeRateDate(stdDate);
                    log.info("{} 일자를 조회하였습니다.", stdDate);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } else {
            log.info("{} 일자 환율정보를 조회할 수 없습니다.", stdDate);
        }

        log.info("{} 에 실행되었습니다.", DateTimeUtil.currentDatetime2());
    }
}
