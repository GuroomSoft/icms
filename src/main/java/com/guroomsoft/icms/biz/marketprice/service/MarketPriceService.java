package com.guroomsoft.icms.biz.marketprice.service;

import com.guroomsoft.icms.biz.marketprice.crawler.FerrousCrawler;
import com.guroomsoft.icms.biz.marketprice.crawler.NonferrousCrawler;
import com.guroomsoft.icms.biz.marketprice.dao.MarketPriceDAO;
import com.guroomsoft.icms.biz.marketprice.dto.FerrousPrice;
import com.guroomsoft.icms.biz.marketprice.dto.NonferrousPrice;
import com.guroomsoft.icms.common.dao.AppConfigDAO;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketPriceService {
    // 비철금속 LME 시세 조회
    private final NonferrousCrawler nonferrousCrawler;
    private final FerrousCrawler ferrousCrawler;
    private final MarketPriceDAO marketPriceDAO;
    private final AppConfigDAO appConfigDAO;

    /**
     * 비철금속 시세정보
     * @return
     */
    @Transactional
    public int loadLMEMarketPrice() throws Exception
    {
        int totalUpdated = 0;
        try {
            List<NonferrousPrice> resultSet = nonferrousCrawler.scrapeMarketPrice();
            for (NonferrousPrice item : resultSet)
            {
                try {
                    int updated = marketPriceDAO.mergeNonferrousPrice(item);
                    if (updated > 0) {
                        totalUpdated++;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("{} findLMEMarketPrice {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return totalUpdated;
    }

    /**
     * 05시 실행
     */
    @Transactional
    @Scheduled(cron = "0 0 5 * * *")
    public void scheduleLMEMarketPrice()
    {
        try{
            loadLMEMarketPrice();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 비철금속 시세 조회
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<NonferrousPrice> findLMEMarketPrice() throws Exception
    {
        try {
            return marketPriceDAO.selectNonferrousMetal();
        } catch (Exception e) {
            log.error("{} findLMEMarketPrice {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getNonferrouseMetalData() throws Exception
    {
        try {
            List<NonferrousPrice> dataList = marketPriceDAO.getNonferrouseMetalData();
            Map<String, Object> resultMap = new LinkedHashMap<>();
            List<String> dateList = new ArrayList<>();
            List<BigDecimal> cuList = new ArrayList<>();
            List<BigDecimal> alList = new ArrayList<>();
            List<BigDecimal> znList = new ArrayList<>();
            List<BigDecimal> pbList = new ArrayList<>();
            List<BigDecimal> niList = new ArrayList<>();
            List<BigDecimal> snList = new ArrayList<>();

            for (NonferrousPrice item : dataList) {
                dateList.add(item.getMnDate());
                cuList.add(item.getCuPrc());
                alList.add(item.getAlPrc());
                znList.add(item.getZnPrc());
                pbList.add(item.getPbPrc());
                niList.add(item.getNiPrc());
                snList.add(item.getSnPrc());
            }
            resultMap.put("date", dateList);
            resultMap.put("cu", cuList);
            resultMap.put("al", alList);
            resultMap.put("zn", znList);
            resultMap.put("pb", pbList);
            resultMap.put("ni", niList);
            resultMap.put("sn", snList);
            return resultMap;
        } catch (Exception e) {
            log.error("{} findLMEMarketPrice {}", this.getClass().getSimpleName(), e.getMessage());
        }

        return null;
    }

    /**
     * 철강재 원자재 시세정보
     * @return
     * @throws Exception
     */
    public Map<String, Object> loadMetalMarketPrice() throws Exception
    {
        try {
            Map<String, Object> rawResult = ferrousCrawler.scrapeMarketPrice();
            removeMarketPriceFerrous((String)rawResult.get("baseDate"));
            List<FerrousPrice> dataList = (List<FerrousPrice>)rawResult.get("data");
            createMarketPriceFerrous(dataList);

            List<FerrousPrice> data = findFerrousPrice((String)rawResult.get("baseDate"));
            Map<String, Object> resultSet = new LinkedHashMap<>();
            String baseDate = getFerrousLastDate();
            resultSet.put("baseDate", baseDate);
            resultSet.put("data", data);
            return resultSet;
        } catch (Exception e) {
            log.error("{} findLMEMarketPrice {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    /**
     * 05시 30분 실행
     */
    @Transactional
    @Scheduled(cron = "0 30 5 * * *")
    public void scheduleMetalMarketPrice()
    {
        try{
            loadMetalMarketPrice();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Transactional
    public int removeMarketPriceFerrous(String mnDate)  throws Exception
    {
        if (StringUtils.isBlank(mnDate)) {
            return 0;
        }

        try {
            return marketPriceDAO.deleteMarketPriceFerrous(mnDate);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }
    @Transactional
    public int createMarketPriceFerrous(List<FerrousPrice> data)  throws Exception
    {
        try {
            return marketPriceDAO.insertMarketPriceFerrous(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 철강재 시세 목록
     * @param baseDate
     * @return
     * @throws Exception
     */
    public List<FerrousPrice> findFerrousPrice(String baseDate) throws Exception
    {
        return marketPriceDAO.selectMarketPriceFerrous(baseDate);
    }

    /**
     * 철강재 시세 최근 일자 조회
     * @return
     * @throws Exception
     */
    public String getFerrousLastDate() throws Exception
    {
        return marketPriceDAO.getFerrousLastDate();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getFerrousHistoryForDashboard() throws Exception
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        List<String> dateList = marketPriceDAO.getFerrousLastDateList();
        String value = appConfigDAO.selectValueByKey(AppConfigService.FERROUS_ITEM_LIST);
        String[] itemList = value.split(",");
        if (itemList == null || itemList.length == 0) {
            return null;
        }
        resultMap.put("date", dateList);
        resultMap.put("itemTitles", itemList);

        for(int i = 0; i < itemList.length; i++){
            List<BigDecimal> tmpList = marketPriceDAO.getFerrousePriceList(itemList[i]);
            resultMap.put(String.format("item%d", i), tmpList);
        }

        return resultMap;
    }


}
