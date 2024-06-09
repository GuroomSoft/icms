package com.guroomsoft.icms.biz.price.dao;

import com.guroomsoft.icms.biz.price.dto.ConsignedPrice;
import com.guroomsoft.icms.biz.price.dto.ConsignedPriceReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table :  고객사 사급단가
 *  HT_CONSIGNED_PRICE
 *
 */
@Mapper
public interface ConsignedPriceDAO {
    int insertConsignedPrice(ConsignedPrice data) throws Exception;
    int deleteConsignedPrice(ConsignedPrice param) throws Exception;
    int mergeConsignedPrice(ConsignedPrice data) throws Exception;
    List<ConsignedPrice> selectConsignedPrice(ConsignedPriceReq cond) throws Exception;
    int getConsignedPriceCount(ConsignedPriceReq cond) throws Exception;
}
