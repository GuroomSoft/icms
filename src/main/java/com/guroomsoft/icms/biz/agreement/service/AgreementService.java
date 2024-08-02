package com.guroomsoft.icms.biz.agreement.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.biz.agreement.dao.AgreementDAO;
import com.guroomsoft.icms.biz.agreement.dto.AgreementDoc;
import com.guroomsoft.icms.biz.agreement.dto.WebhookResponse;
import com.guroomsoft.icms.biz.price.dao.ChangePriceDAO;
import com.guroomsoft.icms.common.event.IcmsEventPublisher;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CDatabaseException;
import com.guroomsoft.icms.common.exception.CInvalidArgumentException;
import com.guroomsoft.icms.common.exception.CModifictionFailException;
import com.guroomsoft.icms.common.service.PdfService;
import com.guroomsoft.icms.util.AppContant;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.w3c.dom.Document;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xml.sax.InputSource;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgreementService {
    private final ChangePriceDAO changePriceDAO;
    private final AgreementDAO agreementDAO;
    private final UserDAO userDAO;
    private final PdfService pdfService;

    private final SpringTemplateEngine templateEngine;

    private final IcmsEventPublisher icmsEventPublisher;

    /**
     * 합의서 생성을 위한 문서 목록 조회
     * @return
     */
    @Transactional(readOnly = true)
    public List<String> getTargetDocList(String announcedDate, String plantCd, List<String> bpList) throws Exception
    {
        if (StringUtils.isBlank(announcedDate) || StringUtils.isBlank(plantCd))
        {
            throw new CInvalidArgumentException();
        }
        if (bpList == null || bpList.isEmpty())
        {
            throw new CInvalidArgumentException();
        }

        try {
            Map<String, Object> cond = new HashMap<>();
            cond.put("plantCd", plantCd);
            cond.put("announcedDate", announcedDate);
            cond.put("bpList", bpList);
            return changePriceDAO.selectChangePriceDocNoList(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 사용자가 지정한 협력사 합의서 등록
     * @param announcedDate
     * @param plantCd
     * @param bpList
     * @throws Exception
     */
    public int createTargetAgreementDoc(String announcedDate, String plantCd, List<String> bpList) throws Exception
    {
        int totalCount = 0;
        List<String> docList = getTargetDocList(announcedDate, plantCd, bpList);

        if (docList == null || docList.isEmpty())
        {
            return totalCount;
        }

        for (String docNo : docList)
        {
            int statusCode = createAgreement(docNo, Long.valueOf(1));
            if (statusCode == 0) totalCount++;
        }

        return totalCount;
    }

    /**
     * 합의서 내용 등록
     * @param srcDocNo
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int createAgreement(String srcDocNo, Long reqUserUid) throws Exception
    {
        int errorCode = 0;

        try {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("pSrcDocNo", srcDocNo);
            param.put("pReqUserUid", reqUserUid);
            agreementDAO.insertAgreement(param);

            errorCode = ((Integer) param.get("errCode")).intValue();
            if (errorCode == 0) {
                String agreementDocNo = (String)param.get("docNo");
                if (StringUtils.isNotBlank(agreementDocNo)) {
                    updateDetailImageString(srcDocNo);
                }
            } else {
                throw new CBizProcessFailException();
            }
            return errorCode;
        } catch (Exception e) {
            log.error(e.getMessage());
            errorCode = -1;
        }

        return errorCode;
    }

    /**
     * 합의서 전송용 데이터 조회
     * @param plantCd
     * @param announcedDate
     * @param bpList
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> findAgreementForSend(String plantCd, String announcedDate, List<String> bpList, String docNo, Long reqUserUid) throws Exception
    {

        if (StringUtils.isBlank(plantCd) || StringUtils.isBlank(announcedDate) || bpList == null || bpList.isEmpty()|| StringUtils.isBlank(docNo))
        {
            throw new CInvalidArgumentException();
        }

        Map<String, Object> cond = new LinkedHashMap<>();
        cond.put("docNo", docNo);
        cond.put("plantCd", plantCd);
        cond.put("announcedDate", announcedDate);
        cond.put("bpList", bpList);
        cond.put("reqUserUid", reqUserUid);
        return agreementDAO.selectAgreementDataOnly(cond);
    }

    public List<Map<String, Object>> findAgreementForEformDocId(List<String> EdormDocids) throws Exception
    {

        if (EdormDocids == null || EdormDocids.isEmpty())
        {
            throw new CInvalidArgumentException();
        }

        Map<String, Object> cond = new LinkedHashMap<>();
        cond.put("EdormDocids", EdormDocids);
        return agreementDAO.selectAgreementDataEformDocId(cond);
    }

    public List<AgreementDoc> findAgreementForDocNo(List<String> DocNos) throws Exception
    {
        if (DocNos == null || DocNos.isEmpty())
        {
            throw new CInvalidArgumentException();
        }
        Map<String, Object> cond = new LinkedHashMap<>();
        cond.put("srcDocNos", DocNos);
        return agreementDAO.selectAgreementDataDocNo(cond);
    }

    /**
     * Agreement 삭제
     * @param DocNos
     * @return
     */
    @Transactional
    public int deleteAgreement(List<String> DocNos) throws Exception
    {
        if ( DocNos == null || DocNos.isEmpty() ) {
            throw new CInvalidArgumentException();
        }
        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("srcDocNos", DocNos);
            return agreementDAO.deleteAgreement(params);
        } catch (Exception e) {
            throw new CModifictionFailException();
        }
    }
    /**
     * 로그 삭제
     * @param eFormDocIds
     * @return
     */
    @Transactional
    public int deleteAgreementLog(List<String> eFormDocIds) throws Exception
    {
        if ( eFormDocIds == null || eFormDocIds.isEmpty() ) {
            throw new CInvalidArgumentException();
        }

        try {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("eFormDocIds", eFormDocIds);
            return agreementDAO.deleteAgreementLog(params);
        } catch (Exception e) {
            throw new CModifictionFailException();
        }
    }

    /**
     * 단건 업데이트
     * @param srcDocNo
     * @return
     */
    @Transactional
    public int updateDetailImageString(String srcDocNo) throws Exception
    {
        if ( StringUtils.isBlank(srcDocNo) ) {
            throw new CInvalidArgumentException();
        }

        try {
            //String detailData = getAgreementDetail(srcDocNo);
            Map<String, String> detailData = getAgreementDetailExt(srcDocNo);
            Map<String, String> params = new LinkedHashMap<>();
            params.put("srcDocNo", srcDocNo);
            params.put("agrDetail", detailData.get("agrDetail"));
            params.put("agrFile", detailData.get("agrFile"));
            return agreementDAO.updateAgreementDetailImage(params);
        } catch (Exception e) {
            throw new CModifictionFailException();
        }
    }

    /**
     * 합의서 리스트 조회
     * @param plantCd
     * @param announcedDate
     * @param bpList
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> findAgreement(String plantCd, String announcedDate, List<String> bpList) throws Exception
    {

        if (StringUtils.isBlank(plantCd) || StringUtils.isBlank(announcedDate) || bpList == null || bpList.isEmpty())
        {
            throw new CInvalidArgumentException();
        }

        Map<String, Object> cond = new LinkedHashMap<>();
        cond.put("plantCd", plantCd);
        cond.put("announcedDate", announcedDate);
        cond.put("bpList", bpList);
        return agreementDAO.selectAgreement(cond);
    }

    /**
     * 가격변경 합의서의 품목별 상세내용을 BASE64 문자열로 변환 후 반환
     * @param docNo
     * @return
     * @throws Exception
     */
    public String getAgreementDetail(String docNo) throws Exception
    {
        List<Map<String, Object>> resultSet = changePriceDAO.selectPriceChangeItem(docNo);

        if (resultSet != null && !resultSet.isEmpty()) {
            int i = 0;
            for (Map<String, Object> item : resultSet)
            {
                item.put("no", Integer.valueOf(++i));
            }
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("itemList", resultSet);
        String htmlString = configTemplate("AgreementTemplate", params);

        String encImage;
        BufferedImage bufferedImage = null;
        bufferedImage = renderHtmlToImage(htmlString);
        encImage = encodeImageToBase64(bufferedImage);

        return String.format("data:image/png;base64,%s", StringUtils.defaultString(encImage));
    }

    public Map<String, String> getAgreementDetailExt(String docNo) throws Exception
    {
        Map<String, String> resultMap = new LinkedHashMap<>();

        List<Map<String, String>> dateList = changePriceDAO.getApplyDateList(docNo);

        List<Map<String, Object>> resultSet = new ArrayList<>();
        Map<String, Object> reportParams = new LinkedHashMap<>();
        String htmlString = "";
        if (dateList.size() == 1)
        {
            resultSet = changePriceDAO.selectPriceChangeItem(docNo);
        } else {
            Map<String, Object> cond = new LinkedHashMap<>();
            cond.put("docNo", docNo);
            cond.put("list", dateList);
            resultSet = changePriceDAO.selectChangePriceSummary(cond);
        }

        if (resultSet != null && !resultSet.isEmpty()) {
            int i = 0;
            for (Map<String, Object> item : resultSet)
            {
                item.put("no", Integer.valueOf(++i));
            }
        }
        if (dateList.size() == 1) {
            reportParams.put("itemList", resultSet);
            htmlString = configTemplate("AgreementTemplate", reportParams);
        } else {
            // 동적 컬럼 타이틀
            for(int i=0; i< dateList.size(); i++){
                Map<String, String> t = dateList.get(i);
                reportParams.put("colTitle" + String.valueOf(i+1), t.get("colTitle"));
            }
            reportParams.put("colspanVal", Integer.valueOf(dateList.size()));
            reportParams.put("itemList", resultSet);

            // log.debug("{}", reportParams.toString());
            htmlString = configTemplate("AgreementTemplate2", reportParams);
        }

        String encImage;
        try {
            BufferedImage bufferedImage = renderHtmlToImage(htmlString);
            encImage = encodeImageToBase64(bufferedImage);
        } catch (IOException | DocumentException e) {
            log.error(e.getMessage());
            encImage = "";
        }

        byte[] t = pdfService.convertHtmlToPdf(htmlString);
        String encPdf = Base64.getEncoder().encodeToString(t);

        encPdf = String.format("data:application/pdf;base64,%s", StringUtils.defaultString(encPdf));
        encImage = String.format("data:image/png;base64,%s", StringUtils.defaultString(encImage));

        resultMap.put("agrFile", encPdf);
        resultMap.put("agrDetail", encImage);
        return resultMap;
    }

    public String configTemplate(final String templateName, final Map<String, Object> params)
    {
        Context context = new Context();
        context.setVariables(params);
        return templateEngine.process(templateName, context);
    }

    /**
     * HTML Source를 레더링한 후 이미지로 반환
     * @param htmlContent
     * @return
     * @throws Exception
     */
    public BufferedImage renderHtmlToImage(String htmlContent) throws Exception
    {
        Document doc = parseStringToDocument(htmlContent);
        Java2DRenderer renderer = new Java2DRenderer(doc, 808, 535);
        return renderer.getImage();
    }

    public Document parseStringToDocument(String xmlString) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xmlString));
        return builder.parse(inputSource);
    }

    public String encodeImageToBase64(BufferedImage image) throws IOException
    {
        // BufferedImage를 바이트 배열로 변환
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // 바이트 배열을 BASE64 문자열로 변환
        String base64String = Base64.getEncoder().encodeToString(imageBytes);

        //log.debug(">>>> {}", base64String);

        return base64String;
    }

    /**
     * 이폼사인 문서 상태 업데이트 알림
     * @param res
     * @throws InterruptedException
     */
    public void notifyUpdateEformsignStatus(WebhookResponse res) throws InterruptedException
    {
        icmsEventPublisher.publishUpdateEfomsignStatus(res);
    }


    /**
     * 가격협의 상태 변경 최근 정보
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getHeaderNotification(Long reqUserUid) throws Exception
    {
        if (reqUserUid.longValue() == 0) {
            throw new CInvalidArgumentException();
        }

        User user = userDAO.selectUserByKey(reqUserUid);
        if (user == null) {
            throw new CInvalidArgumentException();
        }

        Map<String, String> cond = new HashMap<>();
        if (user.getAccountType().equalsIgnoreCase(AppContant.UserType.EXTERNAL.getValue())) {
            if (StringUtils.isNotBlank(user.getBpCd())) {
                cond.put("bpCd", user.getBpCd());
            } else {
                return null;
            }
        } else if (user.getAccountType().equalsIgnoreCase(AppContant.UserType.INTERNAL.getValue())) {
            if (StringUtils.isNotBlank(user.getAccountId())) {
                cond.put("accountId", user.getAccountId());
            } else {
                return null;
            }
        } else {
            return null;
        }

        return agreementDAO.getHeaderNotification(cond);
    }
}
