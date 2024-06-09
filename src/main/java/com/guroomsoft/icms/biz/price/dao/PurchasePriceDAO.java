package com.guroomsoft.icms.biz.price.dao;

import com.guroomsoft.icms.biz.price.dto.PurchasePrice;
import com.guroomsoft.icms.biz.price.dto.PurchasePriceReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table :  공급업체 사급단가
 *  HT_PURCHASE_PRICE
 *
 */
@Mapper
public interface PurchasePriceDAO {
    int insertPurchasePrice(PurchasePrice data) throws Exception;
    int deletePurchasePrice(PurchasePrice param) throws Exception;
    int mergePurchasePrice(PurchasePrice data) throws Exception;
    List<PurchasePrice> selectPurchasePrice(PurchasePriceReq cond) throws Exception;
    int selectPurchasePriceCount(PurchasePriceReq cond) throws Exception;
    String selectExcelContent(int oid);
}
