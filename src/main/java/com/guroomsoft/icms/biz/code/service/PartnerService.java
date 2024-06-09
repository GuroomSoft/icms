package com.guroomsoft.icms.biz.code.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.biz.code.dao.CustomerDAO;
import com.guroomsoft.icms.biz.code.dao.PartnerDAO;
import com.guroomsoft.icms.biz.code.dao.SupplierDAO;
import com.guroomsoft.icms.biz.code.dto.Partner;
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
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerDAO partnerDAO;
    private final CustomerDAO customerDAO;
    private final SupplierDAO supplierDAO;
    private final UserDAO userDAO;
    private final ExcelService excelService;


    /**
     * 협력업체목록
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<Partner> findPartner(Partner cond) throws Exception
    {
        try {
            List<Partner> resultSet = partnerDAO.selectPartner(cond);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            log.error("{} findPartner {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }


    /**
     * 협력업체 상세조회
     * @param bpCd
     * @return
     */
    @Transactional(readOnly = true)
    public Partner findPartnerByKey(String bpCd)  throws Exception
    {
        if (StringUtils.isBlank(bpCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            Partner resultSet = partnerDAO.selectByKey(bpCd);
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
     * 거래처 목록 Helper 용
     * @param cond
     * @return
     */
    public List<Partner> findBpHelper(Map<String, Object> cond) throws Exception
    {
        try {
            return partnerDAO.selectBpHelper(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 명칭 조회
     * @param bpCd
     * @return
     */
    public String getName(String bpCd)
    {
        try {
            Partner partner = findPartnerByKey(bpCd);
            return StringUtils.defaultString(partner.getBpNm());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "";
    }


    /**
     * 여러 건의 협력사 정보를 일괄 등록처리
     * @param list
     * @param reqUserUid
     * @return
     */
    @Transactional
    public int createMultiplePartner(List<Partner> list, Long reqUserUid) throws Exception
    {
        if (list == null || list.isEmpty())
        {
            throw new CInvalidArgumentException();
        }
        // set Default value & validation
        List<Partner> validList = new ArrayList<>();
        for(Partner item : list)
        {
            if (StringUtils.isBlank(item.getBpTaxNm())) {
                item.setBpTaxNm(item.getBpNm());
            }

            item.setRegUid(reqUserUid);
            item.setUseAt(AppContant.CommonValue.YES.getValue());
            item.setDisplayAt(AppContant.CommonValue.YES.getValue());
            if (item.getDisplayOrd() == 0) {
                item.setDisplayOrd(9999);
            }

            if (isValidForRegistration(item))
            {
                validList.add(item);
            }
        }

        if (validList == null || validList.isEmpty()){
            return 0;
        }

        int successCount = 0;
        try {
            int inserted = partnerDAO.insertMultiplePartner(validList);
            if (inserted > 0) successCount++;
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return successCount;
    }

    /**
     * 협력사 마스터 등록
     * @param data
     * @return
     */
    @Transactional
    public int createPartner(Partner data, Long reqUserUid)  throws Exception
    {
        if (!isValidForRegistration(data))
        {
            throw new CInvalidArgumentException();
        }

        try {
            data.setRegUid(reqUserUid);
            Partner newData = setDefault(data);

            if (!isExist(newData.getBpCd())) {
                throw new CAlreadyExistException();
            }

            newData.setContactNm(newData.getCeoNm());

            if (StringUtils.isNotBlank(newData.getBpEmail())) {
                newData.setContactEmail(newData.getBpEmail());
            }

            if (StringUtils.isNotBlank(newData.getBpTelNo()) && newData.getBpTelNo().startsWith("010")) {
                newData.setContactMobile(newData.getBpTelNo());
            }

            // 로그인 정보를 참조하여 이메일 및 전화번호를 업데이트
            User userInfo = userDAO.selectUserByBpCd(newData.getBpCd());

            if (userInfo != null)
            {
                if (StringUtils.isBlank(newData.getBpEmail()) && StringUtils.isNotBlank(userInfo.getEmail()) ) {
                    newData.setContactEmail(userInfo.getEmail());
                }

                if (StringUtils.isBlank(newData.getContactMobile()) && StringUtils.isNotBlank(userInfo.getMobile())) {
                    newData.setContactMobile(userInfo.getMobile());
                }
            }

            int updated = partnerDAO.mergePartner(newData);
            return updated;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CRegistrationFailException();
        }
    }

    /**
     * 여러 건의 협력사 정보를 병합 처리
     * @param dataList
     * @return
     */
    @Transactional
    public Map<String, Object> saveMultiplePartner(List<Partner> dataList, Long reqUsrUid)
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
            try {
                // 유효성 검사
                if ( StringUtils.isBlank(data.getBpCd()) || StringUtils.isBlank(data.getBpNm()) )
                {
                    failCount++;
                    continue;
                }

                // 디폴트 설정
                if (StringUtils.isBlank(data.getUseAt())) data.setUseAt(AppContant.CommonValue.YES.getValue());
                if (StringUtils.isBlank(data.getDisplayAt())) data.setDisplayAt(AppContant.CommonValue.YES.getValue());
                if (data.getDisplayOrd() == null || data.getDisplayOrd().intValue() == 0) data.setDisplayOrd(999);
                if (StringUtils.isBlank(data.getBpTaxNm())) data.setBpTaxNm(data.getBpNm());

                if (StringUtils.isNotBlank(data.getBizRegNo())) {
                    if (StringUtils.isNotBlank(data.getBizRegNo())) {
                        data.setBizRegNo(StringUtils.replace(data.getBizRegNo(), "-", ""));
                        data.setBizRegNo(StringUtils.replace(data.getBizRegNo(), ".", ""));
                    }
                }
                data.setRegUid(reqUsrUid);

                if (StringUtils.isNotBlank(data.getBpEmail())) {
                    data.setContactEmail(data.getBpEmail());
                }

                if (StringUtils.isNotBlank(data.getBpTelNo()) && data.getBpTelNo().startsWith("010")) {
                    data.setContactMobile(data.getBpTelNo());
                }

                // 로그인 정보를 참조하여 이메일 및 전화번호를 업데이트
                User userInfo = userDAO.selectUserByBpCd(data.getBpCd());

                if (userInfo != null)
                {
                    if (StringUtils.isBlank(data.getBpEmail()) && StringUtils.isNotBlank(userInfo.getEmail()) ) {
                        data.setContactEmail(userInfo.getEmail());
                    }

                    if (StringUtils.isBlank(data.getContactMobile()) && StringUtils.isNotBlank(userInfo.getMobile())) {
                        data.setContactMobile(userInfo.getMobile());
                    }
                }

                int inserted = partnerDAO.mergePartner(data);
                if (inserted > 0) {
                    inserted++;
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
     * 협력업체 정보 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyPartner(Partner data, Long reqUserUid) throws Exception
    {
        if (!isValidForModify(data))
        {
            throw new CInvalidArgumentException();
        }

        try {
            data.setRegUid(reqUserUid);
            if (StringUtils.isNotBlank(data.getBizRegNo())) {
                data.setBizRegNo(StringUtils.replace(data.getBizRegNo(), "-", ""));
                data.setBizRegNo(StringUtils.replace(data.getBizRegNo(), ".", ""));
            }

            if (StringUtils.isNotBlank(data.getBpEmail())) {
                data.setContactEmail(data.getBpEmail());
            }

            if (StringUtils.isNotBlank(data.getBpTelNo()) && data.getBpTelNo().startsWith("010")) {
                data.setContactMobile(data.getBpTelNo());
            }

            // 로그인 정보를 참조하여 이메일 및 전화번호를 업데이트
            User userInfo = userDAO.selectUserByBpCd(data.getBpCd());

            if (userInfo != null)
            {
                if (StringUtils.isBlank(data.getBpEmail()) && StringUtils.isNotBlank(userInfo.getEmail()) ) {
                    data.setContactEmail(userInfo.getEmail());
                }

                if (StringUtils.isBlank(data.getContactMobile()) && StringUtils.isNotBlank(userInfo.getMobile())) {
                    data.setContactMobile(userInfo.getMobile());
                }
            }

            int updated = partnerDAO.mergePartner(data);
            if (updated > 0) {
                return updated;
            } else {
                throw new CModifictionFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 협력업체 삭제
     * 공급사 및 고객사 관계정보도 업데이트
     * @param bpCd
     * @return
     */
    @Transactional
    public int removePartner(String bpCd) throws Exception
    {
        if (StringUtils.isBlank(bpCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            int deleted = partnerDAO.deletePartner(bpCd);
            if (deleted > 0) {
                customerDAO.deleteCustomer("", bpCd);
                supplierDAO.deleteSupplier("", "", bpCd);
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
     * BP 상태 업데이트
     * @param bpCd          협력사 코드
     * @param reqUserUid
     * @return
     */
    public int disablePartnerStatus(String bpCd, Long reqUserUid) throws Exception
    {
        if (StringUtils.isBlank(bpCd) ) {
            throw new CInvalidArgumentException();
        }

        Partner data = partnerDAO.selectByKey(bpCd);
        if (data == null) {
            throw new CNotFoundException();
        }

        data.setRegUid(reqUserUid);
        try {

            data.setUseAt(AppContant.CommonValue.NO.getValue());
            data.setRegUid(reqUserUid);
            int updated = partnerDAO.updatePartnerStatus(data);
            if (updated > 0) {
                customerDAO.updateCustomerStatus(data);
                supplierDAO.updateSupplierStatus(data);
                return updated;
            } else {
                throw new CRemoveFailException();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return 0;
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
                && StringUtils.isBlank(data.getBpCd())
                && StringUtils.isBlank(data.getBpNm()) )
        {
            return false;
        }

        return true;
    }

    private boolean isExist(String bpCd)
    {
        try {
            Partner data = partnerDAO.selectByKey(bpCd);
            if (data == null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    private boolean isValidForModify(Partner data)
    {
        if (data == null && StringUtils.isBlank(data.getBpCd()) )
        {
            return false;
        }

        return true;

    }

    /**
     * 협력사 디폴트 값 설정
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
     * 협력사 마스터 대장 엑셀 출력
     * @param fileName
     * @param reportTitle
     * @param pageHeader
     * @param columHeaders
     * @param rows
     * @return
     */
    public ResponseEntity<byte[]> exportToExcelForBp(
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

    /*****************************************
     * 담당 협력사 관련
     ****************************************/

    /**
     * 담당자 협력업체 등록
     * @param data
     * @return
     * @throws Exception
     */
    public int createEmpBp(Partner data) throws Exception
    {
        if(data == null
                || data.getUserUid() == null || data.getUserUid().longValue() == 0
                || StringUtils.isBlank(data.getBpCd()) )
        {
            throw new CInvalidArgumentException();
        }

        partnerDAO.deleteEmpBp(data);
        try {
            return partnerDAO.insertEmpBp(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 담당자 협력업체 삭제
     * @param data
     * @return
     * @throws Exception
     */
    public int removeEmpBp(Partner data) throws Exception
    {
        if(data == null
                || data.getUserUid() == null || data.getUserUid().longValue() == 0
                || StringUtils.isBlank(data.getBpCd()) )
        {
            throw new CInvalidArgumentException();
        }
        try{
            return partnerDAO.deleteEmpBp(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    /**
     * 담당 협력업체 등록
     * @param userUid
     * @param bpList
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int createEmpMultiBp(Long userUid, List<String> bpList, Long reqUserUid) throws Exception
    {
        int totalCount = 0;

        for (String bpCd : bpList)
        {
            Partner data = new Partner();
            data.setUserUid(userUid);
            data.setBpCd(bpCd);
            data.setRegUid(reqUserUid);

            try {
                // partnerDAO.deleteEmpBp(data);
                int updated = partnerDAO.insertEmpBp(data);
                if (updated > 0) {
                    totalCount++;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new CUnknownException();
            }
        }

        return totalCount;
    }

    /**
     * 담당협력사 멀티 삭제
     * @param userUid
     * @param bpList
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeEmpMultiBp(Long userUid, List<String> bpList) throws Exception
    {
        int totalCount = 0;

        for (String bpCd : bpList)
        {
            Partner data = new Partner();
            data.setUserUid(userUid);
            data.setBpCd(bpCd);

            try {
                int deleted = partnerDAO.deleteEmpBp(data);
                if (deleted > 0) {
                    totalCount++;
                }
            } catch (Exception e) {
                throw new CUnknownException();
            }
        }

        return totalCount;
    }

    /**
     * 담당 협력사 목록 조회
     * @param userUid
     * @return
     */
    public List<Map<String, Object>> findPartnerForEmployee(Long userUid) throws Exception
    {
        if (userUid == null || userUid.longValue() == 0) {
            throw new CInvalidArgumentException();
        }

        List<Map<String, Object>> resultSet = new ArrayList<>();
        try {
            resultSet = partnerDAO.selectPartnerForEmployee(userUid, AppContant.CommonValue.YES.getValue());
            if (resultSet != null || resultSet.size() > 0) {
                return resultSet;
            } else {
                throw new CNotFoundException();
            }
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    /**
     * 비담당 협력사 목록 조회
     * @param cond
     * @return
     */
    public List<Map<String, Object>> findPartnerForOthers(Map<String, Object> cond) throws Exception
    {
        if (cond == null) {
            throw new CInvalidArgumentException();
        }
        Long userUid = (Long)cond.get("userUid");
        if (userUid == null || userUid.longValue() == 0) {
            throw new CInvalidArgumentException();
        }

        List<Map<String, Object>> resultSet = new ArrayList<>();
        try {
            resultSet = partnerDAO.selectPartnerForOthers(cond);
            if (resultSet != null || resultSet.size() > 0) {
                return resultSet;
            } else {
                throw new CNotFoundException();
            }
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

}
