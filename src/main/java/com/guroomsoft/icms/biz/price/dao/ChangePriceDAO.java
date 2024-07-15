package com.guroomsoft.icms.biz.price.dao;

import com.guroomsoft.icms.biz.price.dto.ChangePrice;
import com.guroomsoft.icms.biz.price.dto.DetailReq;
import com.guroomsoft.icms.biz.price.dto.PriceChange;
import com.guroomsoft.icms.biz.price.dto.PurchaseItemReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Table :  가격변경이력
 *  HT_CHANGED_PRICE
 *
 */
@Mapper
public interface ChangePriceDAO {
    List<Map<String, Object>> selectTargetBp(Map<String, String> cond) throws Exception;
    List<ChangePrice> selectChangedPrice(DetailReq cond) throws Exception;
    List<Map<String, Object>> selectChangePricePurchaseItem(PurchaseItemReq cond) throws Exception;

    List<Map<String, Object>> selectPriceChangeItem(String docNo) throws Exception;
    List<Map<String, String>> selectAgreementDescList(PurchaseItemReq cond) throws Exception;
    int calculateChangePrice(Map<String, Object> cond) throws Exception;
    int createPriceChangeAll(Map<String, Object> params) throws Exception;

    List<String> selectChangePriceDocNoList(Map<String, Object> cond) throws Exception;
    int closePriceChange(Map<String, Object> params) throws Exception;

    PriceChange getPriceChangeDoc(Map<String, String> cond) throws Exception;

    List<Map<String, Object>> selectChangePriceSummary(Map<String, Object> cond) throws Exception;
    List<Map<String, String>> getApplyDateList(String docNo) throws Exception;
    List<String> getAgreementTitleList(String docNo) throws Exception;

    List<PriceChange> getPriceChangeDocList(Map<String, Object> cond) throws Exception;

    List<ChangePrice> findPurchaseForSap(ChangePrice cond) throws Exception;
    List<ChangePrice> findConsignedForSap(ChangePrice cond) throws Exception;
}
