package com.guroomsoft.icms.biz.code.service;

import com.guroomsoft.icms.biz.code.dao.CustomerDAO;
import com.guroomsoft.icms.biz.code.dao.PartnerDAO;
import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ExcelService;
import com.guroomsoft.icms.sap.JcoClient;
import com.guroomsoft.icms.util.AppContant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 고객사 서비스
 * 회사 - 협력사 기준관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private static String RFC_NAME = "ZSD_ICMS_MAST_BP_SEND";
    private static String RFC_IN_PARAM = "I_BUKRS";     // 회사코드
    private static String RFC_TABLE_NAME = "T_HEADER";
    private final CustomerDAO customerDAO;
    private final PartnerDAO partnerDAO;

    private final ExcelService excelService;
    private final JcoClient jcoClient;

    /**
     * 고객사목록
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<Partner> findCustomer(Partner cond) throws Exception
    {
        try {
            List<Partner> resultSet = customerDAO.selectCustomer(cond);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            log.error("{} findCustomer {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    /**
     * 고객사 상세조회
     * @param corpCd
     * @param bpCd
     * @return
     */
    @Transactional(readOnly = true)
    @Cacheable(value="bpInfo", key="#bpCd")
    public Partner findCustomerByKey(String corpCd, String bpCd)  throws Exception
    {
        if (StringUtils.isBlank(corpCd) && StringUtils.isBlank(bpCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            Partner resultSet = customerDAO.selectCustomerByKey(corpCd, bpCd);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * 거래처 참조
     * @param corpCd
     * @param bpCd
     * @return
     * @throws Exception
     */
    public int getCountOtherRefCount(String corpCd, String bpCd, String useAt) throws Exception
    {
        if (StringUtils.isBlank(corpCd) || StringUtils.isBlank(bpCd)) {
            throw new CInvalidArgumentException();
        }

        Integer cnt = customerDAO.selectOtherRefCount(corpCd, bpCd, useAt);
        if (cnt == null) {
            return 0;
        }
        return cnt.intValue();
    }


    /**
     * 고객사 등록
     * 고객사 등록 시 회사코드가 반드시 지정되어야 한다.
     * @param data
     * @return
     */
    @Transactional
    @CacheEvict(value = "bpInfo", allEntries = true)
    public int createCustomer(Partner data, Long reqUserUid)  throws Exception
    {
        data.setRegUid(reqUserUid);
        try {
            Partner newData = setDefault(data);

            if (!isValidForRegistration(data))
            {
                throw new CInvalidArgumentException();
            }

            // BP 기본정보
            int updated = partnerDAO.mergePartner(data);
            if (updated > 0) {
                // 회사의 고객사 정보
                updated = customerDAO.mergeCustomer(newData);
            }
            return updated;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CRegistrationFailException();
        }
    }

    @Transactional
    @CacheEvict(value = "bpInfo", allEntries = true)
    public Map<String, Object> saveMultipleCustomer(List<Partner> dataList, Long reqUserUid, boolean onlyRelationAt)
    {
        Map<String, Object> resultMap = new HashMap<>();

        int successCount = 0;
        int failCount = 0;

        if (dataList == null || dataList.isEmpty()) {
            resultMap.put("successCount", Integer.valueOf(successCount));
            resultMap.put("failCount", Integer.valueOf(failCount));
            return resultMap;
        }

        for (Partner data : dataList)
        {
            int inserted = 0;
            try {
                if (StringUtils.isBlank(data.getCorpCd()) || StringUtils.isBlank(data.getBpCd())) {
                    failCount++;
                    continue;
                }

                if (onlyRelationAt) {
                    inserted = customerDAO.mergeCustomer(data);
                } else {
                    inserted = createCustomer(data, reqUserUid);
                }

                if (inserted > 0) {
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (Exception e) {
                failCount++;
            }
        }

        resultMap.put("successCount", Integer.valueOf(successCount));
        resultMap.put("failCount", Integer.valueOf(failCount));
        return resultMap;
    }


    /**
     * 고객사 정보 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "bpInfo", allEntries = true)
    public int modifyCustomer(Partner data, Long reqUserUid) throws Exception
    {
        data.setRegUid(reqUserUid);
        try {
            if (!isValidForModify(data))
            {
                throw new CInvalidArgumentException();
            }
            // BP 정보 업데이트
            int updated = partnerDAO.mergePartner(data);
            if (updated > 0) {
                // 회사 고객 정보 업데이트
                updated = customerDAO.mergeCustomer(data);
                if (updated > 0) {
                    return updated;
                } else {
                    throw new CModifictionFailException();
                }
            } else {
                throw new CModifictionFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 고객사 삭제
     * BP 정보 유지 매핑정보에 대한 비활성화 처리
     * @param corpCd
     * @param bpCd
     * @return
     */
    @Transactional
    @CacheEvict(value = "bpInfo", allEntries = true)
    public int removeCustomer(String corpCd, String bpCd) throws Exception
    {
        if (StringUtils.isBlank(corpCd) && StringUtils.isBlank(bpCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            int deleted = customerDAO.deleteCustomer(corpCd, bpCd);
            if (deleted > 0) {
                return deleted;
            } else {
                throw new CRemoveFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 고객 비활성 처리
     * @param corpCd
     * @param bpCd
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "bpInfo", allEntries = true)
    public int disableCustomer(String corpCd, String bpCd, Long reqUserUid) throws Exception
    {
        if (StringUtils.isBlank(corpCd) && StringUtils.isBlank(bpCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            Partner customer = new Partner();
            customer.setCorpCd(corpCd);
            customer.setBpCd(bpCd);
            customer.setUseAt(AppContant.CommonValue.NO.getValue());
            customer.setRegUid(reqUserUid);
            int deleted = customerDAO.updateCustomerStatus(customer);
            if (deleted > 0) {
                return deleted;
            } else {
                throw new CRemoveFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 등록시 유효성 검사
     * @param data
     * @return
     */
    private boolean isValidForRegistration(Partner data)
    {
        if (data == null
                && StringUtils.isBlank(data.getCorpCd())
                && StringUtils.isBlank(data.getBpCd())
                && StringUtils.isBlank(data.getBpNm())
                && StringUtils.isBlank(data.getCeoNm()) )
        {
            return false;
        }

        return true;
    }


    /**
     * 수정 시 유효성 체크
     * @param data
     * @return
     */
    private boolean isValidForModify(Partner data)
    {
        if (data == null && StringUtils.isBlank(data.getCorpCd()) && StringUtils.isBlank(data.getBpCd()) )
        {
            return false;
        }

        return true;

    }

    /**
     * 등록 시 디폴트 값 설정
     * @param data
     * @return
     */
    private Partner setDefault(Partner data)
    {
        if (StringUtils.isBlank(data.getUseAt())) data.setUseAt(AppContant.CommonValue.YES.getValue());
        if (StringUtils.isBlank(data.getDisplayAt())) data.setDisplayAt(AppContant.CommonValue.YES.getValue());
        if (data.getDisplayOrd() == null || data.getDisplayOrd().intValue() == 0) data.setDisplayOrd(999);
        if (StringUtils.isBlank(data.getBpTaxNm())) data.setBpTaxNm(data.getBpNm());

        if (StringUtils.isNotBlank(data.getBizRegNo())) {
            data.setBizRegNo(StringUtils.replace(data.getBizRegNo(), "-", ""));
            data.setBizRegNo(StringUtils.replace(data.getBizRegNo(), ".", ""));
        }

        return data;
    }

    /**
     * 거래처 대장 엑셀 출력
     * @param fileName
     * @param reportTitle
     * @param pageHeader
     * @param columHeaders
     * @param rows
     * @return
     */
    public ResponseEntity<byte[]> exportToExcelForCustomer(
            String fileName,
            String reportTitle,
            Map<String, Object> pageHeader,
            String[] columHeaders,
            List<Partner> rows)
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
        for (Partner data : rows)
        {
            List<Object> row = new ArrayList<Object>();
            row.add(String.valueOf(rowNum++));
            // 컬럼 순서에 해당하는 데이터를 채운다
            row.add( data.getCorpCd() );
            row.add( data.getCorpNm() );
            row.add( data.getBpCd() );
            row.add( data.getBpTaxNm() );
            row.add( data.getBpNm() );
            row.add( data.getCeoNm() );
            row.add( data.getBizRegNo() );
            row.add( data.getBpEmail() );
            row.add( data.getBpTelNo() );
            row.add( data.getPostNo() );
            row.add( data.getBpAdrs() );
            row.add( data.getContactNm() );
            row.add( data.getContactEmail() );
            row.add( data.getContactMobile() );
            row.add( data.getUseAt() );
            row.add( data.getDisplayAt() );
            row.add( data.getBpRemark() );
            row.add( data.getRegDt() );
            row.add( data.getRegAccountId() );

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

    /**
     * SAP로부터 고객사 정보 수신
     * @param corpCd
     * @param reqUserUid
     */
    public int downloadCustomerFromSap(String corpCd, Long reqUserUid)
    {
        try {
            jcoClient.getFunction(RFC_NAME);
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put(RFC_IN_PARAM, corpCd);   // 회사코드
            jcoClient.setImportParam(params);
            jcoClient.runRunction();
            ArrayList<Map<String, String>> dataSet = jcoClient.getTable(RFC_TABLE_NAME);

            // convert to object list
            ArrayList<Partner> dataRows = convertToCustomer(dataSet);

            // save DB
            return loadToDB(dataRows, reqUserUid);
        } catch (Exception e) {
            log.error("👉 Fail to get jco function");
            log.error(e.getMessage());
        }
        return 0;
    }

    /**
     * Sap 수신정보(Map)를 Customer 객체로 변환 후 반환
     * @param dataSet
     * @return
     */
    public ArrayList<Partner> convertToCustomer(ArrayList<Map<String, String>> dataSet)
    {
        if (dataSet == null || dataSet.isEmpty()) {
            return null;
        }

        ArrayList<Partner> resultList = new ArrayList<>();
        for (Map<String, String> dataItem : dataSet)
        {
            Partner dataRow = new Partner();
            dataRow.setCorpCd(dataItem.get("BUKRS"));    // 회사코드           1100
            dataRow.setBpCd(dataItem.get("KUNNR"));     // 고객 코드          100009
            dataRow.setBpNm(dataItem.get("NAME1"));     // 고객명            [경주] DEACHANG SEAT CO.,LTD-GYEONGJU
            dataRow.setBpTaxNm(dataItem.get("NAME1_E"));    // 정식고객명         DEACHANG SEAT CO.,LTD-GYEONGJU
            if (StringUtils.isBlank(dataRow.getBpTaxNm())) {
                dataRow.setBpTaxNm( extractBasicName(dataRow.getBpNm()));
            }
            dataRow.setBizRegNo(dataItem.get("STCD2"));     // 사업자 등록 번호    5058108108 형식 해외 고객사는 전송안함
            String useAt = dataItem.get("LOEVM");
            if (StringUtils.isNotBlank(useAt) && useAt.equalsIgnoreCase("X")) {
                dataRow.setUseAt(AppContant.CommonValue.NO.getValue());
            } else {
                dataRow.setUseAt(AppContant.CommonValue.YES.getValue());
            }
            dataRow.setBpTelNo(StringUtils.defaultString(dataItem.get("TELF1")));      // 전화번호
            dataRow.setPostNo(StringUtils.defaultString(dataItem.get("PSTLZ")));       // 우편번호
            dataRow.setBpAdrs(StringUtils.defaultString(dataItem.get("ZADDR")));       // 주소
            dataRow.setCeoNm(StringUtils.defaultString(dataItem.get("J_1KFREPRE")));   // 대표자
            if (StringUtils.isBlank(dataRow.getCeoNm())) {
                dataRow.setCeoNm(extractUserName(dataRow.getBpNm()));
            }
            dataRow.setBpEmail(StringUtils.defaultString(dataItem.get("AD_SMTPADR"))); // 전자메일 주소
            dataRow.setIfSeq(dataItem.get("ZIFSEQ"));       // 인터페이스 번호     S0000000000000000075
            dataRow.setIfType(StringUtils.defaultString(dataItem.get("ZIFTYPE")));     // 인터페이스 유형     TEST
            dataRow.setIfResult(StringUtils.defaultString(dataItem.get("ZIF_RESULT")));    // 메시지 유형       S : 성공 /  E : 오류
            dataRow.setIfMessage(StringUtils.defaultString(dataItem.get("ZIF_MESSAGE")));  // 메시지

            resultList.add(dataRow);
        }
        return resultList;
    }

    /**
     * SAP로부터 다운로드한 판매처 정보를 DB에 저장 처리
     * @param dataRows
     * @param reqUserUid
     * @return
     */
    public int loadToDB(ArrayList<Partner> dataRows, Long reqUserUid)
    {
        int totalUpdated = 0;
        Long userUid = 1L;
        if (reqUserUid != null) {
            userUid = reqUserUid;
        }

        for (Partner item : dataRows)
        {
            int t = 0;
            try {
                item.setRegUid(userUid);
                int bpUpdated = partnerDAO.mergePartner(item);
                if (bpUpdated > 0) {
                    t = customerDAO.mergeCustomer(item);
                    if (t > 0) {
                        totalUpdated++;
                    }
                }
            } catch (Exception e) {
                log.error("👉 fail to save [{}] {}", item.getBpCd(), item.getBpNm());
            }
        }

        return totalUpdated;
    }

    /**
     * 고객사 신규 등록 목록 조회
     * @param corpCd
     * @param searchWord
     * @param useAt
     * @return
     */
    public List<Partner> helperCustomerForNew(String corpCd, String searchWord, String useAt)
    {
        try {
            return customerDAO.selectCustomerHelperForNew(corpCd, searchWord, useAt);
        } catch (Exception e) {
            return null;
        }
    }

    public String extractUserName(String name)
    {
        String reversNm = StringUtils.reverse(name);
        String result = "";
        if (reversNm.startsWith(")"))
        {
            int idx = reversNm.indexOf("(");
            if (idx == -1)
            {
                return result;
            }

            result = reversNm.substring(1, idx);

            int strLen = StringUtils.length(result);
            System.out.println(">>> string length :: " + String.valueOf(strLen));
            if (strLen < 2) {
                result = "";
            }

            return StringUtils.reverse(result);
        }

        return result;
    }

    public String extractBasicName(String name)
    {
        String userNm = extractUserName(name);
        String result = "";
        if (StringUtils.isNotBlank(userNm))
        {
            result = StringUtils.replace(name, "(" + userNm + ")", "");
        } else {
            result = name;
        }
        return result;
    }

}
