package com.guroomsoft.icms.common.dao;

import com.guroomsoft.icms.common.dto.ExchangeRate;
import com.guroomsoft.icms.common.dto.ExchangeUnit;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Table : 환율정보
 *  HT_EXCHANGE_RATE
 *  환율 정보
 */
@Mapper
public interface ExchangeRateDAO {
    /* 환율정보 관련 */
    List<ExchangeRate> selectExchangeRate(String stdDate) throws Exception;
    int insertExchangeRate(ExchangeRate data) throws Exception;
    int deleteExchangeRate(String stdDate, String curUnit) throws Exception;

    int saveExchangeRateDate(String stdDate);
    String getLastExchangeRateDate();

    List<Map<String, String>> selectExchangeUnit(String displayAt) throws Exception;
    ExchangeUnit getExchangeUnitDefault() throws Exception;

    List<Map<String, Object>> selectBaseRateForLastMonth(String curUnit) throws Exception;
}
