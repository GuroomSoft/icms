package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.biz.code.dto.Partner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table : 공급사 관계정보
 *  MT_BP_RELATION, MV_SUPPLIER
 *
 */
@Mapper
public interface SupplierDAO {
    /* MT_BP_RELATION */
    int mergeSupplier(Partner cond) throws Exception;
    int deleteSupplier(String corpCd, String plantCd, String bpCd) throws Exception;
    int updateSupplierStatus(Partner cond) throws Exception;
    List<Partner> selectSupplier(Partner cond) throws Exception;
    Partner selectSupplierByKey(String corpCd, String plantCd, String bpCd) throws Exception;

    List<Partner> selectSupplierHelperForNew(String plantCd, String searchWord, String useAt) throws Exception;
}
