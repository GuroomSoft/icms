package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.biz.code.dto.Partner;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Table : 협력업체 정보
 *  MT_BIZ_PARTNER
 *
 */
@Mapper
public interface PartnerDAO {
    /* MT_BIZ_PARTNER */
    List<Partner> selectPartner(Partner cond) throws Exception;
    Partner selectByKey(String bpCd) throws Exception;

    List<Partner> selectBpHelper(Map<String, Object> cond) throws Exception;

    int insertMultiplePartner(List<Partner> list) throws Exception;
    int mergePartner(Partner cond) throws Exception;
    int deletePartner(String bpCd) throws Exception;
    int updatePartnerStatus(Partner data) throws Exception;
    int updatePartnerContact(Partner data) throws Exception;

    List<Map<String, Object>> selectPartnerForEmployee(Long userUid, String useAt) throws Exception;
    List<Map<String, Object>> selectPartnerForOthers(Map<String, Object> cond) throws Exception;

    /* MT_EMP_BP */
    int insertEmpBp(Partner data) throws Exception;
    int deleteEmpBp(Partner data) throws Exception;


}
