package com.guroomsoft.icms.biz.templateDoc.dao;

import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDoc;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDtl;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDtlReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Table : 템플릿 문서 및 상세 정보
 *  BT_TEMPLATE_DOC
 *  BT_TEMPLATE_DTL
 *
 */
@Mapper
public interface TemplateDocDAO {
    /* BT_TEMPLATE_DOC */
    List<TemplateDoc> selectTemplateDoc(Map<String, Object> cond) throws Exception;
    TemplateDoc selectTemplateDocByKey(String docNo) throws Exception;
    int insertTemplateDoc(Map<String, Object> data) throws Exception;
    int updateTemplateDoc(Map<String, Object> data) throws Exception;
    int updateDocStatus(TemplateDoc data) throws Exception;
    int deleteTemplateDoc(String docNo) throws Exception;

    /*
     * BT_TEMPLATE_DTL
     */
    List<TemplateDtl> selectTemplateDtl(String docNo) throws Exception;
    TemplateDtl selectTemplateDtlByKey(String docNo, int tdSeq) throws Exception;
    int insertTemplateDtl(Map<String, Object> data) throws Exception;
    int updateTemplateDtl(Map<String, Object> data) throws Exception;
    int deleteTemplateDtl(Map<String, Object> param) throws Exception;
    List<TemplateDtl> selectBpItem(TemplateDtlReq cond) throws Exception;
    int getBpItemCount(TemplateDtlReq cond);
    List<TemplateDtl> selectTemplateDtlWithinDoc(TemplateDtlReq cond) throws Exception;
    int getCountDtlWithinDoc(TemplateDtlReq cond);

}
