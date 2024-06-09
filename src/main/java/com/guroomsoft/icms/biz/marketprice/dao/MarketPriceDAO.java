package com.guroomsoft.icms.biz.marketprice.dao;

import com.guroomsoft.icms.biz.marketprice.dto.FerrousPrice;
import com.guroomsoft.icms.biz.marketprice.dto.NonferrousPrice;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * Table : 시세정보
 *  HT_MARKET_PRICE_NONFERROUS
 *
 */
@Mapper
public interface MarketPriceDAO {
    /* HT_MARKET_PRICE_NONFERROUS */
    int mergeNonferrousPrice(NonferrousPrice data) throws Exception;
    List<NonferrousPrice> selectNonferrousMetal() throws Exception;
    List<NonferrousPrice> getNonferrouseMetalData() throws Exception;


    int insertMarketPriceFerrous(List<FerrousPrice> data) throws Exception;
    int deleteMarketPriceFerrous(String mnDate) throws Exception;
    List<FerrousPrice> selectMarketPriceFerrous(String mnDate) throws Exception;
    String getFerrousLastDate() throws Exception;
    List<String> getFerrousLastDateList() throws Exception;
    List<BigDecimal> getFerrousePriceList(String item) throws Exception;
}
