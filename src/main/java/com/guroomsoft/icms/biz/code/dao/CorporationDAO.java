package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.biz.code.dto.Corporation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table : 회사 정보
 *  MT_CORPORATION
 *
 */
@Mapper
public interface CorporationDAO {
    /* MT_CORPORATION */
    List<Corporation> selectCorporation(Corporation cond) throws Exception;
    Corporation selectCorporationByKey(String corpCd) throws Exception;
    int insertCorporation(Corporation data) throws Exception;
    int updateCorporation(Corporation data) throws Exception;
    int deleteCorporation(String corpCd) throws Exception;
}
