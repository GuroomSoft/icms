package com.guroomsoft.icms.biz.templateDoc.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.biz.code.dao.CodeDAO;
import com.guroomsoft.icms.biz.code.dao.ItemDAO;
import com.guroomsoft.icms.biz.code.dao.PartnerDAO;
import com.guroomsoft.icms.biz.code.dto.Code;
import com.guroomsoft.icms.biz.code.dto.Item;
import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.biz.templateDoc.dao.TemplateDocDAO;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDoc;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDtl;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDtlReq;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ExcelService;
import com.guroomsoft.icms.util.AppContant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 *  템플릿 문서 Service
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateDocService {
    private final TemplateDocDAO templateDocDAO;
    private final ExcelService excelService;
    private final PartnerDAO partnerDAO;
    private final ItemDAO itemDAO;
    private final UserDAO userDAO;
    private final CodeDAO codeDAO;

    /**
     * 템플릿 문서 조회
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<TemplateDoc> findTemplateDoc(Map<String, Object> cond) throws Exception
    {
        List<TemplateDoc> dataset = null;

        try {
            dataset = templateDocDAO.selectTemplateDocNotContent(cond);
            //dataset = templateDocDAO.selectTemplateDoc(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }

        if (dataset == null || dataset.isEmpty()){
            throw new CNotFoundException();
        }

        return dataset;
    }

    /**
     * 템플릿 문서 상세 조회
     * @param docNo
     * @return
     * @throws Exception
     */
    public TemplateDoc findTemplateDocByKey(String docNo) throws Exception
    {
        if (StringUtils.isBlank(docNo)){
            throw new CInvalidArgumentException();
        }

        TemplateDoc dataset = null;

        try {
            dataset = templateDocDAO.selectTemplateDocByKey(docNo);
            if (dataset != null) {
                List<TemplateDtl> dtlDataSet = templateDocDAO.selectTemplateDtl(docNo);
                dataset.setDetails(dtlDataSet);

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }

        if (dataset == null) {
            throw new CNotFoundException();
        }

        return dataset;
    }

    /**
     * 템플릿 문서 등록
     * (참고)문서명 규칙 :
     *   형식1 : 부서 공동문서        - [부서명]문서파일명_버전명_날짜_작성자
     *   형식2 : 같은 팀 문서        - [작성자]문서파일명_버전명_날짜
     *   형식2 : 문서종류가 중요한 경우 - [문서종류]문서파일명_버전명_날짜_작성자
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public TemplateDoc createTemplateDoc(TemplateDoc data, Long requestUserUid) throws Exception
    {
        // 유효성 검사
        if( !isValidNewDoc(data) )
        {
            throw new CInvalidArgumentException();
        }

        // 디폴트 값 설정
        data.setWriterUid(requestUserUid);

        Map<String, Object> params = convertToDocMap(data);
        int errorCode = -1;
        try {
            templateDocDAO.insertTemplateDoc(params);   // 문서등록
            errorCode = ((Integer)params.get("errCode")).intValue();
            String docNo = (String)params.get("docNo"); // 신규문서번호
            if ( (errorCode == 0) && StringUtils.isNotBlank(docNo) )
            {
                List<TemplateDtl> items = data.getDetails();
                if (items != null && !items.isEmpty())
                {
                    // 상세 등록을 위한 전처리
                    for (TemplateDtl item : items)
                    {
                        item.setDocNo(docNo);
                    }

                    // 상세항목 등록
                    createTemplateDtlList(items, requestUserUid);
                }

                return findTemplateDocByKey(docNo);
            } else {
                log.info((String)params.get("errMsg"));
                throw new CRegistrationFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 신규 문서 유효성 검사
     * @param doc
     * @return
     */
    private boolean isValidNewDoc(TemplateDoc doc)
    {
        if( doc == null
                || StringUtils.isBlank(doc.getDocTitle())
                || StringUtils.isBlank(doc.getDocFilename())
                || StringUtils.isBlank(doc.getDocContent()) )
        {
            return false;
        }

        return true;
    }
    /**
     * 템플릿 문서정보 수정
     * @param doc
     * @param requestUserUid
     * @return
     */
    @Transactional
    public int modifyTemplateDoc(TemplateDoc doc, Long requestUserUid) throws Exception
    {
        // 유효성 체크
        if (doc == null || StringUtils.isBlank(doc.getDocNo()))
        {
            throw new CInvalidArgumentException();
        }

        if (doc.getDocStatus().equals(AppContant.CommonValue.YES.getValue()))
        {
            throw new CViolationCloseException();
        }
        doc.setModUid(requestUserUid);

        Map<String, Object> params = convertToDocMap(doc);
        int errorCode = -1;
        try {
            templateDocDAO.updateTemplateDoc(params);
            errorCode = ((Integer) params.get("errCode")).intValue();
            if (errorCode < 0) {
                throw new CModifictionFailException();
            }

            List<TemplateDtl> details = doc.getDetails();
            if ((details != null) && !details.isEmpty())
            {
                Map<String, Object> cond = new HashMap<>();
                cond.put("docNo", doc.getDocNo());
                templateDocDAO.deleteTemplateDtl(cond); // 세부항목 삭제 후 등록처리
                for (TemplateDtl item : details) {
                    item.setDocNo(doc.getDocNo());
                    item.setRegUid(requestUserUid);
                    Map<String, Object> dataMap = convertToItemMap(item);
                    templateDocDAO.insertTemplateDtl(dataMap);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }

        return 1;
    }

    /**
     * 템플릿 문서 및 세부항목 삭제처리
     * @param docNo
     * @return
     */
    @Transactional
    public int removeTemplateDoc(String docNo)
    {
        // 유효성 검사
        if (StringUtils.isBlank(docNo)) {
            throw new CInvalidArgumentException();
        }

        try {
            TemplateDoc doc = findTemplateDocByKey(docNo);
            if (doc == null) {
                throw new CNotFoundException();
            }
            if (doc.getDocStatus().equals(AppContant.CommonValue.YES.getValue()))
            {
                throw new CViolationCloseException();
            }
        } catch (Exception e) {
            throw new CNotFoundException();
        }

        try {
            // 상세항목 삭제
            Map<String, Object> cond = new HashMap<>();
            cond.put("docNo", docNo);
            templateDocDAO.deleteTemplateDtl(cond);
            return templateDocDAO.deleteTemplateDoc(docNo); // 문서삭제
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 템플릿 문서 확정 처리
     * @param doc
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int confirmDoc(TemplateDoc doc, Long reqUserUid) throws Exception
    {
        if (doc == null || StringUtils.isBlank(doc.getDocNo())) {
            throw new CInvalidArgumentException();
        }

        try {
            doc.setDocStatus(AppContant.CommonValue.YES.getValue());
            doc.setModUid(reqUserUid);
            return templateDocDAO.updateDocStatus(doc);
        } catch (Exception e) {
            throw new CDatabaseException();
        }
    }

    /**
     * 템플릿 문서 취소 처리
     * @param doc
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int cancelDoc(TemplateDoc doc, Long reqUserUid) throws Exception
    {
        if (doc == null || StringUtils.isBlank(doc.getDocNo())) {
            throw new CInvalidArgumentException();
        }

        try {
            doc.setDocStatus(AppContant.CommonValue.NO.getValue());
            doc.setModUid(reqUserUid);
            return templateDocDAO.updateDocStatus(doc);
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }


    /**
     * 템플릿 문서 객체를 Map으로 변환
     * @param doc
     * @return
     */
    public Map<String, Object> convertToDocMap(TemplateDoc doc)
    {
        if (doc == null) {
            return new LinkedHashMap<>();
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("docTitle", StringUtils.defaultString(doc.getDocTitle()));
        data.put("docFilename", StringUtils.defaultString(doc.getDocFilename()));
        data.put("writerUid", doc.getWriterUid());
        data.put("docPwd", StringUtils.defaultString(doc.getDocPwd()));
        data.put("docRemark", StringUtils.defaultString(doc.getDocRemark()));
        data.put("docContent", StringUtils.defaultString(doc.getDocContent()));

        if (StringUtils.isNotBlank(doc.getDocNo())) {
            data.put("docNo", StringUtils.defaultString(doc.getDocNo()));
        }

        if (StringUtils.isNotBlank(doc.getDocStatus())) {
            data.put("docStatus", StringUtils.defaultString(doc.getDocStatus()));
        }

        if (doc.getModUid() != null && doc.getModUid().longValue()>0) {
            data.put("modUid", new BigDecimal(doc.getModUid()));
        }

        return data;
    }


    /**
     * 유효성 체크
     * @param items
     * @return
     */
    public Map<String, Object> checkValidItems(List<TemplateDtl> items)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        List<TemplateDtl> validItems = new ArrayList<>();
        List<TemplateDtl> invalidItems = new ArrayList<>();
        if (items == null || items.isEmpty()) {
            resultMap.put("validItems", validItems);
            resultMap.put("invalidItems", invalidItems);

            return resultMap;
        }

        for (TemplateDtl item : items)
        {
            // 전체 적용
            if (StringUtils.isBlank(item.getPlantCd()))
            {
                item.setInvalidMessage("플랜트 코드 누락");
                invalidItems.add(item);
                continue;
            }
            if (StringUtils.isBlank(item.getBpCd()))
            {
                item.setInvalidMessage("협력사코드 누락");
                invalidItems.add(item);
                continue;
            } else {
                if (StringUtils.isBlank(item.getBpNm())) {
                    try {
                        Partner p = partnerDAO.selectByKey(item.getBpCd());
                        if (p != null && StringUtils.isNotBlank(p.getBpNm())){
                            item.setBpNm(p.getBpNm());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            //if ( StringUtils.isBlank(item.getCarModel()) )
            //{
            //    item.setInvalidMessage("차종 누락");
            //    invalidItems.add(item);
            //    continue;
            //}
            if ( StringUtils.isBlank(item.getPcsItemNo()) )
            {
                item.setInvalidMessage("매입품번 누락");
                invalidItems.add(item);
                continue;
            } else {
                if (StringUtils.isBlank(item.getPcsItemNm())) {
                    try {
                        Item itemInfo = itemDAO.selectItemByKey(item.getPcsItemNo());
                        if (itemInfo != null && StringUtils.isNotBlank(itemInfo.getItemNm())) {
                            item.setPcsItemNm(itemInfo.getItemNm());
                        }
                    } catch (Exception e) {
                    }
                }
            }

            if ( StringUtils.isBlank(item.getPartType()) )
            {
                item.setInvalidMessage("부품코드 누락");
                invalidItems.add(item);
                continue;
            } else {
                if (StringUtils.isBlank(item.getPartTypeNm())) {
                    try {
                        Code c = codeDAO.selectCodeByKey(AppContant.CodeGroupValue.PART_TYPE.getValue(), item.getPartType());
                        if (c != null && StringUtils.isNotBlank(c.getCdNm())) {
                            item.setPartTypeNm(c.getCdNm());
                        }
                    } catch (Exception e) {
                    }
                }
            }

            if ( StringUtils.isBlank(item.getSubItemNo()) )
            {
                item.setInvalidMessage("SUB 품번 누락");
                invalidItems.add(item);
                continue;
            } else {
                if (StringUtils.isBlank(item.getSubItemNo())) {
                    try {
                        Item itemInfo = itemDAO.selectItemByKey(item.getSubItemNo());
                        if (itemInfo != null && StringUtils.isNotBlank(itemInfo.getItemNm())) {
                            item.setSubItemNm(itemInfo.getItemNm());
                        }
                    } catch (Exception e) {
                    }
                }
            }


            if ( StringUtils.upperCase(item.getPartType()).equals(AppContant.PartType.RESALE.getValue()) ) {
                // 사급인 경우 사급품 협력사 코드 체크
                if ( StringUtils.isBlank(item.getPcsSubItemBp()) ) {
                    item.setInvalidMessage("부품구분이 사급인 경우 사급품 협력사 코드 필수");
                    invalidItems.add(item);
                    continue;
                } else {
                    if (StringUtils.isBlank(item.getPcsSubItemBpNm())) {
                        try {
                            Partner p = partnerDAO.selectByKey(item.getPcsSubItemBp());
                            if (p != null && StringUtils.isNotBlank(p.getBpNm())){
                                item.setPcsSubItemBpNm(p.getBpNm());
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }

            // US 디폴트 값 설정
            if (item.getUs() == null || item.getUs().intValue() == 0) {
                item.setInvalidMessage("US 는 1 이상이 정수만 허용");
                item.setUs(Integer.valueOf(1));
            }

            if ( StringUtils.upperCase(item.getPartType()).equals(AppContant.PartType.DEVELOPMENT.getValue()) )
            {
                // 직개발인 경우
                if (StringUtils.isBlank(item.getRawMaterialCd()) )
                {
                    item.setInvalidMessage("직개발 부품 - 소재코드 누락");
                    invalidItems.add(item);
                    continue;
                } else {
                    if (StringUtils.isBlank(item.getRawMaterialNm())) {
                        try {
                            Code c = codeDAO.selectCodeByKey(AppContant.CodeGroupValue.MATERIAL_TYPE.getValue(), item.getRawMaterialCd());
                            if (c != null && StringUtils.isNotBlank(c.getCdNm())) {
                                item.setRawMaterialNm(c.getCdNm());
                            }
                        } catch (Exception e) {
                        }
                    }
                }
                if ( StringUtils.isBlank(item.getMaterialCd()) )
                {
                    item.setInvalidMessage("직개발 부품 - 재질코드 누락");
                    invalidItems.add(item);
                    continue;
                }

                // Net 중량
                if (item.getNetWeight() == null || item.getNetWeight().doubleValue() == 0 ) {
                    item.setInvalidMessage("직개발 부품 - Net 중량 누락");
                    invalidItems.add(item);
                    continue;
                }

                // 철판(10) 또는 파이프(15) 이외에는 투입중량 작성
                if (!(item.getRawMaterialCd().trim().equals("10") || item.getRawMaterialCd().trim().equals("15")) )
                {
                    if  (item.getInputWeight() == null || item.getInputWeight().doubleValue() == 0)
                    {
                        item.setInvalidMessage("직개발 부품 철판 / 파이프 이외에 투입 중량 필수항목");
                        invalidItems.add(item);
                        continue;
                    }
                }

                // 고정값 설정
                // 철판(10) 또는 파이프(15) 이외에는 투입중량 작성
                if (item.getRawMaterialCd().trim().equals("10"))            // 철판
                {
                    item.setSpecificGravity(new BigDecimal(7.85));
                    item.setSlittLossRate(new BigDecimal(0.42));
                    item.setToLossRate(new BigDecimal(0.6));
                } else if (item.getRawMaterialCd().trim().equals("15")) {   // 파이프
                    item.setSpecificGravity(new BigDecimal(7.85));
                    item.setSlittLossRate(null);
                    item.setToLossRate(new BigDecimal(2.0));
                } else {
                    item.setSpecificGravity(null);
                    item.setSlittLossRate(null);
                    item.setToLossRate(null);
                }

                // 재관비율 및 외주재관비
                item.setMatAdminRate(new BigDecimal(2.0));
                item.setOsMatAdminRate(new BigDecimal(0.0));
            } else if ( StringUtils.upperCase(item.getPartType()).equals(AppContant.PartType.TRANSACTION.getValue()) ) {
                // 직거래인 경우
                if (StringUtils.isBlank(item.getRawMaterialCd()) )
                {
                    item.setInvalidMessage("직개발 부품 - 소재코드 누락");
                    invalidItems.add(item);
                    continue;
                } else {
                    if (StringUtils.isBlank(item.getRawMaterialNm())) {
                        try {
                            Code c = codeDAO.selectCodeByKey(AppContant.CodeGroupValue.MATERIAL_TYPE.getValue(), item.getRawMaterialCd());
                            if (c != null && StringUtils.isNotBlank(c.getCdNm())) {
                                item.setRawMaterialNm(c.getCdNm());
                            }
                        } catch (Exception e) {
                        }
                    }
                }

                if ( StringUtils.isBlank(item.getMaterialCd()) )
                {
                    item.setInvalidMessage("직개발 부품 - 재질코드 누락");
                    invalidItems.add(item);
                    continue;
                }

                // Net 중량
                if (item.getNetWeight() == null || item.getNetWeight().doubleValue() == 0 ) {
                    item.setInvalidMessage("직개발 부품 - Net 중량 누락");
                    invalidItems.add(item);
                    continue;
                }

                // 철판(10) 또는 파이프(15) 이외에는 투입중량 작성
                if (!(item.getRawMaterialCd().trim().equals("10") || item.getRawMaterialCd().trim().equals("15")) )
                {
                    if  (item.getInputWeight() == null || item.getInputWeight().doubleValue() == 0)
                    {
                        item.setInvalidMessage("직개발 부품 철판 / 파이프 이외에 투입 중량 필수항목");
                        invalidItems.add(item);
                        continue;
                    }
                }

                // 고정값 설정
                // 철판(10) 또는 파이프(15) 이외에는 투입중량 작성
                if (item.getRawMaterialCd().trim().equals("10"))            // 철판
                {
                    item.setSpecificGravity(new BigDecimal(7.85));
                    item.setSlittLossRate(new BigDecimal(0.42));
                    item.setToLossRate(new BigDecimal(0.6));
                } else if (item.getRawMaterialCd().trim().equals("15")) {   // 파이프
                    item.setSpecificGravity(new BigDecimal(7.85));
                    item.setSlittLossRate(null);
                    item.setToLossRate(new BigDecimal(2.0));
                } else {
                    item.setSpecificGravity(null);
                    item.setSlittLossRate(null);
                    item.setToLossRate(null);
                }

                item.setMatAdminRate(new BigDecimal(2.0));
                item.setOsMatAdminRate(new BigDecimal(1.0));
            } else if ( StringUtils.upperCase(item.getPartType()).equals(AppContant.PartType.RESALE.getValue()) ) {
                // 사급인 경우
                item.setMatAdminRate(new BigDecimal(0.0));
                item.setOsMatAdminRate(new BigDecimal(1.0));
            }

            validItems.add(item);
        }

        resultMap.put("validItems", validItems);
        resultMap.put("invalidItems", invalidItems);
        return resultMap;
    }

    /**
     * 템플릿 세부항목 등록
     * @param item
     * @param requestUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public TemplateDtl createTemplateDtl(TemplateDtl item, Long requestUserUid) throws Exception
    {
        // 유효성 검사
        if( !isValidNewDtl(item) )
        {
            throw new CInvalidArgumentException();
        }

        // 디폴트 값 설정
        item.setRegUid(requestUserUid);


        Map<String, Object> params = convertToItemMap(item);

        int errorCode = -1;
        try {
            templateDocDAO.insertTemplateDtl(params);
            errorCode = ((Integer) params.get("errCode")).intValue();
            log.info((String) params.get("errMsg"));

            if (errorCode == 0)
            {
                BigDecimal tdSeq = (BigDecimal)params.get("tdSeq");
                if (tdSeq != null && tdSeq.intValue() > 0) {
                    return findTemplateDtlByKey(item.getDocNo(), tdSeq.intValue());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }

        return null;
    }

    /**
     * 템플릿 상세 유효성 체크
     * @param item
     * @return
     */
    private boolean isValidNewDtl(TemplateDtl item)
    {
        if( item == null
                || StringUtils.isBlank(item.getDocNo())
                || StringUtils.isBlank(item.getPlantCd())
                || StringUtils.isBlank(item.getBpCd())
                || StringUtils.isBlank(item.getPcsItemNo())
                || StringUtils.isBlank(item.getSubItemNo())
                || StringUtils.isBlank(item.getCarModel())
                || StringUtils.isBlank(item.getPartType()) )
        {
            return false;
        }

        return true;
    }

    private boolean isValidDtl(TemplateDtl item)
    {
        if( item == null
                || StringUtils.isBlank(item.getDocNo())
                || item.getTdSeq() == null || item.getTdSeq().intValue() < 1
                || StringUtils.isBlank(item.getPlantCd())
                || StringUtils.isBlank(item.getBpCd())
                || StringUtils.isBlank(item.getPcsItemNo())
                || StringUtils.isBlank(item.getSubItemNo())
                || StringUtils.isBlank(item.getCarModel())
                || StringUtils.isBlank(item.getPartType()) )
        {
            return false;
        }

        return true;
    }

    /**
     * 상세내역 목록을 일괄등록 처리
     * @param items
     * @param requestUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int createTemplateDtlList(List<TemplateDtl> items, Long requestUserUid) throws Exception
    {
        int totalCount = 0;

        if (items == null || items.isEmpty()) {
            throw new CInvalidArgumentException();
        }

        for (TemplateDtl item : items)
        {
            if (!isValidNewDtl(item)) {
                log.debug(">>> Skip invalid detail item {} {} {}", item.getBpCd(), item.getPcsItemNo(), item.getSubItemNo() );
                continue;
            }

            item.setRegUid(requestUserUid);

            Map<String, Object> params = convertToItemMap(item);
            log.debug(">>>>>> {}", params.toString());
            try {
                int errorCode = -1;
                try {
                    int t = templateDocDAO.insertTemplateDtl(params);
                    errorCode = ((Integer)params.get("errCode")).intValue();
                    log.debug(">>>>>> errorMessage :::: {}", (String)params.get("errMsg"));

                    if (errorCode == 0) {
                        totalCount++;
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                    continue;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return totalCount;
    }

    @Transactional
    public int modifyTemplateDtl(TemplateDtl item, Long requestUserUid) throws Exception
    {
        // 유효성 검사
        if( !isValidDtl(item) )
        {
            throw new CInvalidArgumentException();
        }

        // 디폴트 값 설정
        if (requestUserUid.longValue() > 0){
            item.setModUid(requestUserUid);
        }

        Map<String, Object> params = convertToItemMap(item);

        int errorCode = -1;
        try {
            templateDocDAO.updateTemplateDtl(params);
            errorCode = (int)params.get("errCode");
            if (errorCode == 0) {
                return 1;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
        return -1;
    }

    /**
     * 템플릿 세부항목 삭제
     * @param docNo
     * @param tdSeq
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeTemplateDtl(String docNo, Integer tdSeq) throws Exception
    {
        if (StringUtils.isBlank(docNo))
        {
            throw new CInvalidArgumentException();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("docNo", docNo);
        if (tdSeq != null) {
            params.put("tdSeq", tdSeq);
        }

        try {
            return templateDocDAO.deleteTemplateDtl(params);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 템플릿 세부항목 목록 조회
     * @param docNo
     * @return
     * @throws Exception
     */
    @Transactional
    public List<TemplateDtl> findTemplateDtl(String docNo) throws Exception
    {
        try {
            return templateDocDAO.selectTemplateDtl(docNo);
        } catch (Exception e) {
            throw new CDatabaseException();
        }
    }

    /**
     * 템플릿 세부항목 조회 (단건)
     * @param docNo
     * @param tdSeq
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public TemplateDtl findTemplateDtlByKey(String docNo, int tdSeq) throws Exception
    {
        if (StringUtils.isBlank(docNo) || tdSeq < 1) {
            throw new CInvalidArgumentException();
        }

        try {
            return templateDocDAO.selectTemplateDtlByKey(docNo, tdSeq);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }


    public Map<String, Object> convertToItemMap(TemplateDtl item)
    {
        if (item == null) {
            return new LinkedHashMap<>();
        }

        Map<String, Object> data = new LinkedHashMap<>();

        data.put("docNo", StringUtils.defaultString(item.getDocNo()));
        data.put("plantCd", StringUtils.defaultString(item.getPlantCd()));
        data.put("bpCd", StringUtils.defaultString(item.getBpCd()));
        data.put("docOrder", StringUtils.defaultString(item.getDocOrder()));
        data.put("carModel", StringUtils.defaultString(item.getCarModel()));
        data.put("pcsItemNo", StringUtils.defaultString(item.getPcsItemNo()));
        data.put("pcsItemNm", StringUtils.defaultString(item.getPcsItemNm()));
        data.put("partType", StringUtils.defaultString(item.getPartType()) );
        data.put("pcsSubItemBp", StringUtils.defaultString(item.getPcsSubItemBp()) );
        data.put("subItemNo", StringUtils.defaultString(item.getSubItemNo()) );
        data.put("subItemNm", StringUtils.defaultString(item.getSubItemNm()) );
        data.put("rawMaterialCd", StringUtils.defaultString(item.getRawMaterialCd()) );
        data.put("materialCd", StringUtils.defaultString(item.getMaterialCd()) );
        data.put("materialNm", StringUtils.defaultString(item.getMaterialNm()) );
        data.put("us", item.getUs() );
        data.put("steelGrade", StringUtils.defaultString(item.getSteelGrade()));
        data.put("mSpec", StringUtils.defaultString(item.getMSpec()));
        data.put("mType", StringUtils.defaultString(item.getMType()));
        data.put("thickThick", item.getThickThick() );
        data.put("widthOuter", item.getWidthOuter() );
        data.put("heightInLen", item.getHeightInLen() );
        data.put("blWidth", item.getBlWidth() );
        data.put("blLength", item.getBlLength() );
        data.put("blCavity", item.getBlCavity() );
        data.put("netWeight", item.getNetWeight() );
        data.put("toLossRate", item.getToLossRate() );
        data.put("inputWeight", item.getInputWeight() );
        data.put("scrapWeight", item.getScrapWeight() );
        data.put("scrapRecoveryRate", item.getScrapRecoveryRate() );
        data.put("writerId", item.getWriterId() );

        if (item.getTdSeq() != null) {
            data.put("tdSeq", item.getTdSeq());
        }

        if (item.getRegUid() != null) {
            data.put("regUid", item.getRegUid());
        }

        if (item.getModUid() != null ) {
            data.put("modUid", item.getModUid());
        }

        return data;
    }

    /**
     * 협력사 품목 조회
     * @param cond
     * @return
     */
    public List<TemplateDtl> findBpItem(TemplateDtlReq cond) throws Exception
    {
        try {
            List<TemplateDtl> resultSet = templateDocDAO.selectBpItem(cond);
            return resultSet;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 협력사 품목 count
     * @param cond
     * @return
     */
    public int getBpItemCount(TemplateDtlReq cond)
    {
        return templateDocDAO.getBpItemCount(cond);
    }

    /**
     * 템플릿 문서내의 상세 항목 리스트 조회
     * @param cond
     * @return
     * @throws Exception
     */
    public List<TemplateDtl> findTemplateDtlWithinDoc(TemplateDtlReq cond) throws Exception
    {
        try {
            List<TemplateDtl> resultSet = templateDocDAO.selectTemplateDtlWithinDoc(cond);
            return resultSet;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    public int getCountDtlWithinDoc(TemplateDtlReq cond)
    {
        return templateDocDAO.getCountDtlWithinDoc(cond);
    }
    /**
     * 문서목록 엑셀 출력
     * @param fileName
     * @param reportTitle
     * @param pageHeader
     * @param columHeaders
     * @param rows
     * @return
     */
    public ResponseEntity<byte[]> exportToExcel(
            String fileName,
            String reportTitle,
            Map<String, Object> pageHeader,
            String[] columHeaders,
            List<TemplateDoc> rows)
    {
        HttpHeaders httpHeader = null;
        try {
            httpHeader = excelService.createHttpHeaders(fileName);
        } catch (UnsupportedEncodingException e) {
            return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
        }

        if (rows == null || rows.isEmpty()) {
            return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
        }

        // Sheet 에 대한 정의
        Map<String, Object> sheet = new HashMap<String, Object>();
        sheet.put("sheetName", reportTitle);
        sheet.put("reportTitle", reportTitle);
        // Page Header 정의 - 조회조건
        sheet.put("searchCondition", pageHeader);

        // 컬럼 헤더 정의
        List<String> colTitles = Arrays.asList(columHeaders);
        sheet.put("colTitle", colTitles);

        // 데이터 정의
        List<List<Object>> rowData = new ArrayList<List<Object>>();
        int rowNum = 1;
        for (TemplateDoc data : rows)
        {
            List<Object> row = new ArrayList<Object>();
            row.add(String.valueOf(rowNum++));
            // 컬럼 순서에 해당하는 데이터를 채운다
            row.add( data.getDocNo() );
            row.add( data.getDocTitle() );
            row.add( data.getDocFilename() );
            row.add( data.getWriteDt() );
            row.add( data.getWriterId() );
            row.add( data.getDocStatusNm() );
            row.add( data.getConfirmDt() );
            row.add( data.getDocRemark() );
            row.add( data.getRegDt() );
            row.add( data.getRegAccountId() );
            row.add( data.getModDt() );
            row.add( data.getModAccountId() );

            rowData.add(row);
        }

        sheet.put("rowData", rowData);

        //
        Map<String, Object> sheetMap = new HashMap<String, Object>();
        sheetMap.put("sheet1", sheet);

        try {
            byte[] contents = excelService.exportToExcel2(fileName, sheetMap);
            if (contents == null) {
                return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<byte[]>(contents, httpHeader, HttpStatus.OK);
            }
        } catch (IOException e) {
            log.error("IOException:::Export to Excel");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<byte[]> exportItemToExcel(
            String fileName,
            String reportTitle,
            Map<String, Object> pageHeader,
            String[] columHeaders,
            List<TemplateDtl> rows)
    {
        HttpHeaders httpHeader = null;
        try {
            httpHeader = excelService.createHttpHeaders(fileName);
        } catch (UnsupportedEncodingException e) {
            return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
        }

        if (rows == null || rows.isEmpty()) {
            return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
        }

        // Sheet 에 대한 정의
        Map<String, Object> sheet = new HashMap<String, Object>();
        sheet.put("sheetName", reportTitle);
        sheet.put("reportTitle", reportTitle);
        // Page Header 정의 - 조회조건
        sheet.put("searchCondition", pageHeader);

        // 컬럼 헤더 정의
        List<String> colTitles = Arrays.asList(columHeaders);
        sheet.put("colTitle", colTitles);

        // 데이터 정의
        List<List<Object>> rowData = new ArrayList<List<Object>>();
        int rowNum = 1;
        for (TemplateDtl data : rows)
        {
            List<Object> row = new ArrayList<Object>();
            row.add(String.valueOf(rowNum++));
            // 컬럼 순서에 해당하는 데이터를 채운다
            row.add( data.getPlantCd() );       // 플랜트코드
            row.add( data.getPlantNm() );       // 플랜트
            row.add( data.getBpCd() );          // 거래처코드
            row.add( data.getBpNm() );          // 거래처명
            row.add( data.getCarModel() );      // 차종
            row.add( data.getPcsItemNo() );     // 매입품번
            row.add( data.getPcsItemNm() );     // 매입품명
            row.add( data.getPartType() );      // 부품구분
            row.add( data.getPartTypeNm() );    // 부품구분명
            row.add( data.getPcsSubItemBp() );      // 사급품 협력사 코드
            row.add( data.getPcsSubItemBpNm() );    // 사급품 협력사명
            row.add( data.getSubItemNo() );     // SUB 품번
            row.add( data.getSubItemNm() );     // SUB 품명
            row.add( data.getRawMaterialCd() ); // 원소재코드
            row.add( data.getRawMaterialNm() ); // 원소재명
            row.add( data.getMaterialCd() );    // 재질코드
            row.add( data.getMaterialNm() );    // 재질명
            row.add( data.getUs() );            // US
            row.add( data.getSteelGrade() );    // 강종
            row.add( data.getMSpec() );         // M-Spec
            row.add( data.getMType() );         // M-Type
            row.add( data.getThickThick() );    // 두께/두께
            row.add( data.getWidthOuter() );    // 가로/외경
            row.add( data.getHeightInLen() );   // 세로/투입길이
            row.add( data.getBlWidth() );       // BL-가로
            row.add( data.getBlLength() );      // BL-세로
            row.add( data.getBlCavity() );      // BL-CAVITY
            row.add( data.getNetWeight() );     // NET중량(Kg)
            row.add( data.getSpecificGravity() );   // 비중
            row.add( data.getSlittLossRate() );     // SLITT LOSS 율(%)
            row.add( data.getToLossRate() );        // TO LOSS 율(%)
            row.add( data.getInputWeight() );       // 투입중량(Kg)
            row.add( data.getScrapWeight() );       // Scrap 중량(Kg/EA)
            row.add( data.getScrapRecoveryRate() ); // Scrap 회수율(%)
            row.add( data.getMatAdminRate() );      // 재관비율(%)
            row.add( data.getOsMatAdminRate() );    // 외주재관비율(%)
            row.add( data.getWriterId() );          // 작성자 ID
            row.add( data.getDocNo() );             // 문서번호

            rowData.add(row);
        }

        sheet.put("rowData", rowData);

        //
        Map<String, Object> sheetMap = new HashMap<String, Object>();
        sheetMap.put("sheet1", sheet);

        try {
            byte[] contents = excelService.exportToExcel2(fileName, sheetMap);
            if (contents == null) {
                return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<byte[]>(contents, httpHeader, HttpStatus.OK);
            }
        } catch (IOException e) {
            log.error("IOException:::Export to Excel");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
    }

}
