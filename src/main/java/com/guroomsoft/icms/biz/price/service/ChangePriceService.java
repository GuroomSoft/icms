package com.guroomsoft.icms.biz.price.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.biz.code.dao.PartnerDAO;
import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.biz.code.service.SupplierService;
import com.guroomsoft.icms.biz.price.dao.ChangePriceDAO;
import com.guroomsoft.icms.biz.price.dto.ChangePrice;
import com.guroomsoft.icms.biz.price.dto.DetailReq;
import com.guroomsoft.icms.biz.price.dto.PriceChange;
import com.guroomsoft.icms.biz.price.dto.PurchaseItemReq;
import com.guroomsoft.icms.common.event.IcmsEventPublisher;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CDatabaseException;
import com.guroomsoft.icms.common.exception.CInvalidArgumentException;
import com.guroomsoft.icms.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePriceService {
    private final ChangePriceDAO changePriceDAO;
    private final UserDAO userDAO;
    private final PartnerDAO partnerDAO;

    private final IcmsEventPublisher icmsEventPublisher;

    /**
     * 단가 변경 협력사 목록 조회
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findTargetBpList(String announcedDate, String plantCd) throws Exception
    {
        if (StringUtils.isBlank(announcedDate) || StringUtils.isBlank(plantCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            Map<String, String> cond = new HashMap<>();
            cond.put("plantCd", plantCd);
            cond.put("announcedDate", announcedDate);
            return changePriceDAO.selectTargetBp(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    @Transactional(readOnly = true)
    public List<ChangePrice> findChangedPrice(DetailReq cond) throws Exception
    {
        try {

            return changePriceDAO.selectChangedPrice(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 매입품번 기준 가격변경 정보 조회
     * @param cond
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findChangePricePurchaseItem(PurchaseItemReq cond) throws Exception
    {
        if (cond == null
                || StringUtils.isBlank(cond.getAnnouncedDate())
                || StringUtils.isBlank(cond.getPlantCd()) )
        {
            throw new CInvalidArgumentException();
        }
        try {
            return changePriceDAO.selectChangePricePurchaseItem(cond);
        } catch (Exception e) {
            log.debug(e.getMessage());
            throw new CDatabaseException();
        }
    }

    public PriceChange getPriceChangeDoc(String plantCd, String announcedDate, String bpCd) throws Exception
    {
        if (StringUtils.isBlank(plantCd) || StringUtils.isBlank(announcedDate) || StringUtils.isBlank(bpCd)) {
            throw new CInvalidArgumentException();
        }

        Map<String, String> cond = new LinkedHashMap<>();
        cond.put("plantCd", plantCd);
        cond.put("announcedDate", announcedDate);
        cond.put("bpCd", bpCd);

        return changePriceDAO.getPriceChangeDoc(cond);
    }

    /**
     * 가격 결정합의서
     * @param cond
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAgreementContent(PurchaseItemReq cond, Long reqUserUid) throws Exception
    {
        if (cond == null
                || StringUtils.isBlank(cond.getAnnouncedDate())
                || StringUtils.isBlank(cond.getPlantCd())
                || StringUtils.isBlank(cond.getBpCd()) )
        {
            throw new CInvalidArgumentException();
        }

        cond.setBpList(null);
        try {
            User user = userDAO.selectUserByKey(reqUserUid);
            Partner bp = partnerDAO.selectByKey(cond.getBpCd());
            PriceChange doc = getPriceChangeDoc(cond.getPlantCd(), cond.getAnnouncedDate(), cond.getBpCd());

            Map<String, Object> resultMap = new LinkedHashMap<>();
            List<Map<String, Object>> dataList = changePriceDAO.selectChangePricePurchaseItem(cond);
            resultMap.put("agreementDate", DateTimeUtil.currentDate());
            if(user != null) {
                resultMap.put("deptNm", StringUtils.defaultString(user.getOrgNm()));              // 부서명
                resultMap.put("consignor", StringUtils.defaultString(user.getAccountName()));     // 위탁자명
                resultMap.put("userSign", String.format("data:image/png;base64,%s", StringUtils.defaultString(user.getUserSign())) );         // 발송자 서명
            } else {
                resultMap.put("deptNm", "-");        // 부서명
                resultMap.put("consignor", "-");     // 위탁자명
                resultMap.put("userSign", "");
            }

            if (bp != null) {
                resultMap.put("bpNm", SupplierService.extractBasicName(bp.getBpNm()));            // 협력사명
                resultMap.put("consignee", bp.getCeoNm());      // 대표자명
            } else {
                resultMap.put("bpNm", "-");          // 협력사명
                resultMap.put("consignee", "-");     // 대표자명
            }

            if (doc != null) {
                resultMap.put("approvalStatus", StringUtils.defaultString(doc.getApprovalStatus(), "BG"));
            }

            List<String> descList = getSummaryDesc(cond);
            for(int i = 1; i < 6; i++){
                if (descList.size() >= i) {
                    resultMap.put("remark"+String.valueOf(i), descList.get(i-1));
                } else {
                    resultMap.put("remark"+String.valueOf(i), "");
                }
            }

            resultMap.put("details", dataList);
            return resultMap;
        } catch (Exception e) {
            log.debug(e.getMessage());
            throw new CDatabaseException();
        }
    }


    /**
     * 가격결정합의서 요약 정보
     * @param cond
     * @return
     */
    public List<String> getSummaryDesc(PurchaseItemReq cond)
    {
        List<String> resultSet = new ArrayList<>();
        try {
            List<Map<String, String>> dataSet = changePriceDAO.selectAgreementDescList(cond);
            for (Map<String, String> item : dataSet)
            {
                String yy = item.get("applyDate").substring(2,4);
                String mm = item.get("applyDate").substring(4,6);

                resultSet.add(String.format("%s년 %s월 : %s 변동", yy, mm, StringUtils.defaultString(item.get("rawMaterialNm"))));
            }
            return resultSet;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    @Transactional
    public int calculateChangePrice(String plantCd, String announcedDate, Long reqUserUid) throws Exception
    {
        if (StringUtils.isBlank(plantCd) || StringUtils.isBlank(announcedDate)) {
            throw new CInvalidArgumentException();
        }
        int errorCode = 0;

        try {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("plantCd", plantCd);
            param.put("announcedDate", announcedDate);
            param.put("reqUserUid", reqUserUid);
            changePriceDAO.calculateChangePrice(param);
            errorCode = ((Integer) param.get("errCode")).intValue();
            if (errorCode < 0) {
                throw new CBizProcessFailException();
            }
            return errorCode;
        } catch (CBizProcessFailException e) {
            throw new CBizProcessFailException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }



    /**
     * 공시단가 확정 시 전체 플랜트에 대한 가격변경 처리
     * @param docNo
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public void createPriceChangeAll(String docNo, Long reqUserUid) throws Exception
    {
        try {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("pDocNo", docNo);
            param.put("pReqUserUid", reqUserUid);
            changePriceDAO.createPriceChangeAll(param);

            if (((Integer)param.get("pErrCode")).intValue() < 0 ) {
                throw new CBizProcessFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException(e.getMessage());
        }
    }

    /**
     * 선택한 문서 확정처리
     * @param docs
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int confirmPriceChangeDoc(List<String> docs, Long reqUserUid) throws Exception
    {
        int totalCount = 0;
        if (docs == null || docs.isEmpty()) {
            throw new CInvalidArgumentException();
        }

        for (String docNo : docs) {
            try {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("pDocNo", docNo);
                params.put("pReqUserUid", reqUserUid);
                changePriceDAO.closePriceChange(params);
                if (((Integer)params.get("pErrCode")).intValue() < 0 ) {
                    continue;
                }
                totalCount++;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        icmsEventPublisher.publishClosePriceChange(docs);

        return totalCount;
    }

}
