package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.biz.code.dto.Partner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table : 고객
 *  MT_CUSTOMER_REPLATION, MV_CUSTOMER
 *
 */
@Mapper
public interface CustomerDAO {
    /* MT_CUSTOMER_REPLATION */
    List<Partner> selectCustomer(Partner cond) throws Exception;
    Partner selectCustomerByKey(String corpCd, String bpCd) throws Exception;
    Integer selectOtherRefCount(String corpCd, String bpCd, String useAt) throws Exception;
    int mergeCustomer(Partner cond) throws Exception;
    int updateCustomerStatus(Partner cond) throws Exception;
    int deleteCustomer(String corpCd, String bpCd) throws Exception;
    List<Partner> selectCustomerHelperForNew(String corpCd, String searchWord, String useAt) throws Exception;
}
