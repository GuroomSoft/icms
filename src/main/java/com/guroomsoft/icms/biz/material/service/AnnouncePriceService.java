package com.guroomsoft.icms.biz.material.service;

import com.guroomsoft.icms.biz.code.dao.CodeDAO;
import com.guroomsoft.icms.biz.code.dto.Code;
import com.guroomsoft.icms.biz.material.dao.AnnouncePriceDAO;
import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import com.guroomsoft.icms.biz.material.dto.AnnouncePriceDetail;
import com.guroomsoft.icms.common.dao.ExchangeRateDAO;
import com.guroomsoft.icms.common.dto.ExchangeUnit;
import com.guroomsoft.icms.common.event.IcmsEventPublisher;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncePriceService {
    private final AnnouncePriceDAO announcePriceDAO;
    private final CodeDAO codeDAO;
    private final ExchangeRateDAO exchangeRateDAO;
    private final ExcelService excelService;
    private final IcmsEventPublisher icmsEventPublisher;


    /**
     * 공시단가 목록 조회
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<AnnouncePrice> findAnnouncePrice(Map<String, String> cond) throws Exception
    {
        List<AnnouncePrice> resultSet = new ArrayList<>();
        try {
            resultSet = announcePriceDAO.selectAnnouncePriceDoc(cond);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 공시단가 상세조회
     * @param docNo
     * @return
     */
    @Transactional(readOnly = true)
    public AnnouncePrice findAnnouncePriceByKey(String docNo) throws Exception
    {
        if (StringUtils.isBlank(docNo))
        {
            throw new CInvalidArgumentException();
        }

        try {
            AnnouncePrice doc = announcePriceDAO.selectAnnouncePriceDocByKey(docNo);
            if (doc == null){
                throw new CNotFoundException();
            } else {
                AnnouncePriceDetail cond = new AnnouncePriceDetail();
                cond.setDocNo(docNo);
                List<AnnouncePriceDetail> items = announcePriceDAO.selectAnnouncePriceDtl(cond);
                if (items != null && !items.isEmpty())
                {
                    doc.setDetails(items);
                } else {
                    throw new CNotFoundException();
                }
                return doc;
            }
        } catch (CNotFoundException e) {
            throw new CNotFoundException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 공시단가 엑셀 다운로드
     * @param fileName
     * @param reportTitle
     * @param pageHeader
     * @param columHeaders
     * @param rows
     * @return
     */
    public ResponseEntity<byte[]> exportToExcelForAnnouncePriceDetail(
            String fileName,
            String reportTitle,
            Map<String, Object> pageHeader,
            String[] columHeaders,
            List<AnnouncePriceDetail> rows)
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
        for (AnnouncePriceDetail data : rows)
        {
            List<Object> row = new ArrayList<Object>();
            row.add(String.valueOf(rowNum++));
            // 컬럼 순서에 해당하는 데이터를 채운다
            row.add( data.getRawMaterialCd() );
            row.add( data.getRawMaterialNm() );
            row.add( data.getSteelGrade() );
            row.add( data.getMaterialCd() );
            row.add( data.getMaterialNm() );
            row.add( data.getMatUnitPrice() );
            row.add( data.getScrapPrice() );
            row.add( data.getMpRemark() );
            row.add( data.getRegDt() );
            row.add( data.getRegAccountId() );
            row.add( data.getModDt() );
            row.add( data.getModAccountId() );

            rowData.add(row);
        }

        sheet.put("rowData", rowData);

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

    /**
     * 공시단가 문서 단순 조회
     * @param docNo
     * @return
     */
    @Transactional(readOnly = true)
    public AnnouncePrice findAnnouncePriceByKeySimple(String docNo)
    {
        if (StringUtils.isBlank(docNo))
        {
            return null;
        }

        try {
            return announcePriceDAO.selectAnnouncePriceDocByKeySimple(docNo);
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 문서 확정여부
     * @param docNo
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isCloseDoc(String docNo)
    {
        AnnouncePrice doc = findAnnouncePriceByKeySimple(docNo);
        if ((doc == null) || StringUtils.isBlank(doc.getDocStatus()))
        {
            return false;
        }

        if (doc.getDocStatus().equalsIgnoreCase(AppContant.CommonValue.YES.getValue())) {
            return true;
        }

        return false;
    }

    /**
     * 공시단가 문서 등록
     * 공시단가문서 및 업로드 원본 및 상세 내역을 등록처리
     * @param data
     * @return
     */
    @Transactional
    public AnnouncePrice createAnnouncePriceDoc(AnnouncePrice data, Long reqUserUid) throws Exception
    {
        if ( !isValid(data) ) {
            throw new CInvalidArgumentException();
        }

        // set default
        if (StringUtils.isBlank(data.getDocStatus())) {
            data.setDocStatus(AppContant.CommonValue.NO.getValue());
        }

        data.setRegUid(reqUserUid);
        data.setWriterUid(reqUserUid);

        try {
            int inserted = announcePriceDAO.insertAnnouncePriceDoc(data);
            if (inserted > 0) {
                createMultiAnnouncePriceDtl(data, reqUserUid);
                return findAnnouncePriceByKey(data.getDocNo());
            } else {
                throw new CRegistrationFailException();
            }
        } catch (CRegistrationFailException e) {
            throw new CRegistrationFailException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 공시단가 문서만 수정처리
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyAnnouncePriceDoc(AnnouncePrice data, Long reqUserUid) throws Exception
    {
        if (data == null || StringUtils.isBlank(data.getDocNo())) {
            throw new CInvalidArgumentException();
        }

        // 마감된 문서는 수정 안됨.
        if (isCloseDoc(data.getDocNo()))
        {
            throw new CViolationCloseException();
        }

        // set default

        data.setModUid(reqUserUid);
        try {
            int updated = announcePriceDAO.updateAnnouncePriceDoc(data);

            return updated;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 공시단가 수정 업로드 처리
     * @param data
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public AnnouncePrice modifyAnnouncePriceDocUpload(AnnouncePrice data, Long reqUserUid) throws Exception
    {
        if (data == null || StringUtils.isBlank(data.getDocNo())) {
            throw new CInvalidArgumentException();
        }

        // 마감된 문서는 수정 안됨.
        if (isCloseDoc(data.getDocNo()))
        {
            throw new CViolationCloseException();
        }

        data.setModUid(reqUserUid);
        try {
            int updated = announcePriceDAO.updateAnnouncePriceDoc(data);
            if (updated > 0)
            {
                if (data.getDetails() != null && !data.getDetails().isEmpty())
                {
                    // 전체 공시단가 삭제
                    AnnouncePriceDetail param = new AnnouncePriceDetail();
                    param.setDocNo(data.getDocNo());
                    announcePriceDAO.deleteAnnouncePriceDtl(param);
                    createMultiAnnouncePriceDtl(data, reqUserUid);
                }
                return findAnnouncePriceByKey(data.getDocNo());
            }

            throw new CModifictionFailException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 전처리 후
     * 공시단가 일괄 등록 처리
     * @param doc
     * @return
     * @throws Exception
     */
    @Transactional
    public int createMultiAnnouncePriceDtl(AnnouncePrice doc, Long reqUserUid)
    {
        int apSeq = 0;

        List<AnnouncePriceDetail> sourceList = new ArrayList<>();
        List<AnnouncePriceDetail> validList = new ArrayList<>();


        if (doc == null || doc.getDetails().isEmpty())
        {
            return 0;
        }

        sourceList = doc.getDetails();

        Collections.sort(sourceList, new AnnouncePriceDetail());

        // 전처리
        for (AnnouncePriceDetail item : sourceList)
        {
            // 유효성 체크
            if (StringUtils.isBlank(item.getMaterialCd())
                    || StringUtils.isBlank(item.getMaterialNm())
                    || StringUtils.isBlank(item.getBgnDate()) ) {
                continue;
            }

            // 디폴트 값 설정
            item.setDocNo(doc.getDocNo());
            item.setCountryCd(doc.getCountryCd());
            item.setRegUid(reqUserUid);

            // 확정 데이터 중 더 최근 적용 시작일이 존재하는지 체크
            // 만약 존재한다면 skip
            int newerCount = getNewerMaterialPriceCount(item);
            if (newerCount > 0) {
                continue;
            }

            // 적용 종료일
            if (StringUtils.isBlank(item.getEndDate())) {
                item.setEndDate(AppContant.DEFAULT_APPLY_END_DATE);
            }

            // 환율
            if (StringUtils.isBlank(item.getCurrencyUnit()))
            {
                Code currencyCode = null;
                try {
                    currencyCode = codeDAO.selectCodeByKey(AppContant.CodeGroupValue.COUNTRY.getValue(), doc.getCountryCd());
                    if (currencyCode != null && StringUtils.isNotBlank(currencyCode.getAddData1())) {
                        item.setCurrencyUnit(currencyCode.getAddData1());
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            // 순번
            // 멀티 생성인 경우 순번을 지정해야 함.
            item.setApSeq(Integer.valueOf(++apSeq));
            validList.add(item);
        }
        // 등록처리
        int inserted = 0;
        int totalCount = 0;
        try {

            for (AnnouncePriceDetail item : validList)
            {
                inserted = announcePriceDAO.insertAnnouncePriceDtl(item);
                if (inserted>0) totalCount++;
            }
            return totalCount;
        } catch (Exception e) {
            throw new CRegistrationFailException();
        }
    }


    /**
     * 공시단가 문서 유효성 검증
     * @param data
     * @return
     */
    private boolean isValid(AnnouncePrice data)
    {
        if (data == null
                || StringUtils.isBlank(data.getDocTitle())
                || StringUtils.isBlank(data.getDocStatus())
                || StringUtils.isBlank(data.getCountryCd()) )
        {
            return false;
        }
        return true;
    }


    public int getNewerMaterialPriceCount(AnnouncePriceDetail cond)
    {

        try {
            return announcePriceDAO.getNewerMaterialCount(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }


    /**
     * 공시단가 문서 확정처리
     * 공시단가 마감처리 후 메시지 발송하여 가격변경 자료를 생성
     *    SP_CLOSE_ANNOUNCE_PRICE_DOC 호출 후
     *    이벤트 알림
     * @param doc
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public void confirmDoc(AnnouncePrice doc, Long reqUserUid) throws Exception
    {
        if (doc == null || StringUtils.isBlank(doc.getDocNo())) {
            throw new CInvalidArgumentException();
        }

        // 현재 문서 상태 체크
        AnnouncePrice curDoc = findAnnouncePriceByKey(doc.getDocNo());
        if (curDoc == null) {
            throw new CNotFoundException();
        }

        if (StringUtils.isNotBlank(curDoc.getDocStatus()) && curDoc.getDocStatus().equalsIgnoreCase(AppContant.CommonValue.YES.getValue()))
        {
            throw new CAlreadyCloseException();
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("pDocNo", doc.getDocNo());
            params.put("pRequestUserUid", reqUserUid);

            announcePriceDAO.closeAnnouncePriceDoc(params);
            if (((Integer) params.get("errCode")).intValue() == 0)
            {
                doc.setRegUid(reqUserUid);
                // 공시단가 문서 확정 이벤트 알림
                icmsEventPublisher.publishCloseAnnouncePriceDocEvent(doc);
            } else {
                throw new CBizProcessFailException();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CModifictionFailException();
        }
    }

    @Transactional
    public void cancelDoc(AnnouncePrice doc, Long reqUserUid) throws Exception
    {
        if (doc == null || StringUtils.isBlank(doc.getDocNo())) {
            throw new CInvalidArgumentException();
        }

        // 현재 문서 상태 체크
        AnnouncePrice curDoc = findAnnouncePriceByKey(doc.getDocNo());
        if (curDoc == null) {
            throw new CNotFoundException();
        }

        if (StringUtils.isNotBlank(curDoc.getDocStatus())
                && !curDoc.getDocStatus().equalsIgnoreCase(AppContant.CommonValue.YES.getValue()))
        {
            return ;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("pDocNo", doc.getDocNo());
        params.put("pReqUserUid", reqUserUid);

        announcePriceDAO.cancelAnnouncePriceDoc(params);
        if (((Integer) params.get("pErrCode")).intValue() < 0)
        {
            throw new CBizProcessFailException();
        }
    }

    /**
     * 공시단가 문서 삭제
     * @param docNo
     * @return
     */
    @Transactional
    public int removeAnnouncePriceDoc(String docNo)
    {
        if (StringUtils.isBlank(docNo)) {
            throw new CInvalidArgumentException();
        }

        if (isCloseDoc(docNo))
        {
            throw new CViolationCloseException();
        }

        try {
            AnnouncePriceDetail param = new AnnouncePriceDetail();
            param.setDocNo(docNo);
            // 상세 삭제
            announcePriceDAO.deleteAnnouncePriceDtl(param);
            // 문서 삭제
            return announcePriceDAO.deleteAnnouncePriceDoc(docNo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CRemoveFailException();
        }
    }

    /**
     * 공시단가 목록 조회
     * @param cond
     * @return
     * @throws Exception
     */
    public List<AnnouncePriceDetail> findAnnouncePriceDtl(AnnouncePriceDetail cond) throws Exception
    {
        if (cond == null || StringUtils.isBlank(cond.getDocNo())) {
            throw new CInvalidArgumentException();
        }

        List<AnnouncePriceDetail> resultSet = null;
        try {
            return announcePriceDAO.selectAnnouncePriceDtl(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }


    /**
     * 최종 공시단가 조회
     * @param cond
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> findAnnouncePriceLast(AnnouncePriceDetail cond) throws Exception
    {
        List<Map<String, Object>> dataSet = new ArrayList<>();
        try {
            dataSet = announcePriceDAO.selectAnnouncePriceLast(cond);
            if (dataSet == null) {
                throw new CNotFoundException();
            }
            return dataSet;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 재질 공시단가 이력 조회
     * @param countryCd
     * @param materialCd
     * @return
     */
    public List<AnnouncePriceDetail> findAnnouncePriceHistory(String countryCd, String materialCd, String materialNm) throws Exception
    {
        if (StringUtils.isBlank(countryCd) || StringUtils.isBlank(materialCd) || StringUtils.isBlank(materialNm)) {
            throw new CInvalidArgumentException();
        }

        try {
            return announcePriceDAO.selectAnnouncePriceHistory(countryCd, materialCd, materialNm);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }


    /**
     * 공시단가 상세조회
     * @param docNo
     * @param apSeq
     * @return
     */
    public AnnouncePriceDetail findAnnouncePriceDtlByKey(String docNo, Integer apSeq) throws Exception
    {
        if (StringUtils.isBlank(docNo)
                || apSeq == null || (apSeq.intValue() == 0))
        {
            throw new CInvalidArgumentException();
        }

        try {
            AnnouncePriceDetail cond = new AnnouncePriceDetail();
            cond.setDocNo(docNo);
            cond.setApSeq(apSeq);
            return announcePriceDAO.selectAnnouncePriceDtlByKey(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    public List<AnnouncePriceDetail> findAnnouncePriceListSimple(AnnouncePriceDetail cond) throws Exception
    {
        return announcePriceDAO.selectAnnouncePriceDtlSimple(cond);
    }

    // todo 적용일자 기준으로 재질목록을 조회하는 쿼리 추가 throws Exception
    @Transactional
    public AnnouncePriceDetail createAnnouncePriceDtl(AnnouncePriceDetail data, Long reqUserUid) throws Exception
    {
        data.setBgnDate(StringUtils.replace(data.getBgnDate(), "-", ""));

        // 유효성 체크
        if (data == null
                || StringUtils.isBlank(data.getDocNo())
                || StringUtils.isBlank(data.getMaterialCd())
                || StringUtils.isBlank(data.getMaterialNm())
                || StringUtils.isBlank(data.getBgnDate()) )
        {
            throw new CInvalidArgumentException();
        }

        // 문서 마감여부
        if (isCloseDoc(data.getDocNo()))
        {
            throw new CViolationCloseException();
        }

        // 확정된 재질 단가 정보 중 더 최근 적용일이 존재하는 지 체크
        int newCount = getNewerMaterialPriceCount(data);
        if (newCount > 0) {
            log.error("createAnnouncePriceDtl - 확정 데이터 중 더 최근 적용일정보가 존재합니다.");
            throw new CInvalidArgumentException();
        }

        // 중복체크
        AnnouncePriceDetail cond = new AnnouncePriceDetail();
        cond.setMaterialCd(data.getMaterialCd());
        cond.setMaterialNm(data.getMaterialNm());
        cond.setBgnDate(data.getBgnDate());

        List<AnnouncePriceDetail> oldValue = announcePriceDAO.selectAnnouncePriceDtlSimple( cond );
        if ( (oldValue != null) && (oldValue.size() > 0) )
        {
            throw new CAlreadyExistException();
        }

        data.setRegUid(reqUserUid);
        try {
            // 디폴트 값 설정
            if (StringUtils.isBlank(data.getCountryCd())) {
                data.setCountryCd(codeDAO.selectDefaultCode(AppContant.CodeGroupValue.COUNTRY.getValue()));
            }

            // 적용종료일
            if (StringUtils.isBlank(data.getEndDate())) {
                data.setEndDate(AppContant.DEFAULT_APPLY_END_DATE);
            }

            // 통화단위
            if (StringUtils.isBlank(data.getCurrencyUnit())) {
                ExchangeUnit eu = exchangeRateDAO.getExchangeUnitDefault();
                if (eu != null) {
                    data.setCurrencyUnit(eu.getExchgUnit());
                } else {
                    data.setCurrencyUnit("KRW");
                }
            }

            if (data.getBfMatUnitPrice() == null) data.setBfMatUnitPrice(new BigDecimal(0));
            if (data.getMatUnitPrice() == null) data.setMatUnitPrice(new BigDecimal(0));
            if (data.getDiffMatPrice() == null) data.setDiffMatPrice(new BigDecimal(0));
            if (data.getBfScrapPrice() == null) data.setBfScrapPrice(new BigDecimal(0));
            if (data.getScrapPrice() == null) data.setScrapPrice(new BigDecimal(0));
            if (data.getDiffScrapPrice() == null) data.setDiffScrapPrice(new BigDecimal(0));

            int inserted = announcePriceDAO.insertAnnouncePriceDtl(data);
            if (inserted > 0) {
                return findAnnouncePriceDtlByKey(data.getDocNo(), data.getApSeq());
            } else {
                throw new CRegistrationFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }
    @Transactional
    public int  modifyAnnouncePriceDtl(AnnouncePriceDetail data, Long reqUserUid)  throws Exception
    {
        // 유효성 체크
        if (data == null
                || (data.getApSeq() == null) || (data.getApSeq().intValue() == 0))
        {
            throw new CInvalidArgumentException();
        }

        // 문서 마감여부
        if (isCloseDoc(data.getDocNo()))
        {
            throw new CViolationCloseException();
        }

        try {
            data.setModUid(reqUserUid);
            log.info(">>>>>>>> updateAnnouncePriceDtl");
            int updated = announcePriceDAO.updateAnnouncePriceDtl(data);
            if (updated == 0) {
                throw new CModifictionFailException();
            } else {
                return updated;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }
    @Transactional
    public int removeAnnouncePriceDtl(AnnouncePriceDetail param) throws Exception
    {
        if ( (param == null) || StringUtils.isBlank(param.getDocNo()) || (param.getApSeq().intValue() == 0) )
        {
            throw new CInvalidArgumentException();
        }
        // 문서 마감여부
        if (isCloseDoc(param.getDocNo()))
        {
            throw new CViolationCloseException();
        }

        int deleted = 0;
        try {

            deleted = announcePriceDAO.deleteAnnouncePriceDtl(param);
            if (deleted > 0)
            {
                return deleted;
            }

            throw new CRemoveFailException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException(e.getMessage());
        }

    }

    /**
     * 고시일 목록 조회
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<Map<String, String>> findAnnouncedDateList() throws Exception
    {
        try {
            return announcePriceDAO.selectAnnouncedDate();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    @Transactional(readOnly = true)
    public List<String> findApplyDate(String docNo)  throws Exception
    {
        if (StringUtils.isBlank(docNo)) {
            throw new CInvalidArgumentException();
        }

        try {
            return announcePriceDAO.selectApplyDate(docNo);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }


}
