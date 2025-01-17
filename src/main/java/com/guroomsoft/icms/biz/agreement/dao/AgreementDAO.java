package com.guroomsoft.icms.biz.agreement.dao;

import com.guroomsoft.icms.biz.agreement.dto.AgreementDoc;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Table :
 *  HT_AGREEMENT
 */
@Mapper
public interface AgreementDAO {
    int insertAgreement(Map<String, Object> param) throws Exception;
    List<Map<String, Object>> selectAgreement(Map<String, Object> cond) throws Exception;
    List<Map<String, Object>> selectAgreementDataOnly(Map<String, Object> cond) throws Exception;
    List<Map<String, Object>> selectAgreementDataEformDocId(Map<String, Object> cond) throws Exception;
    List<AgreementDoc> selectAgreementDataDocNo(Map<String, Object> cond) throws Exception;

    int updateAgreementDetailImage(Map<String, String> param) throws Exception;
    int insertEformWebhook(Map<String, Object> param) throws Exception;

    int deleteAgreement(Map<String, Object> param) throws Exception;
    int deleteAgreementLog(Map<String, Object> param) throws Exception;

    List<Map<String, Object>> getHeaderNotification(Map<String, String> param) throws Exception;
}
