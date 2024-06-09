package com.guroomsoft.icms.biz.price.service;

import com.guroomsoft.icms.biz.code.dao.PlantDAO;
import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.biz.price.dao.ConsignedPriceDAO;
import com.guroomsoft.icms.biz.price.dto.ConsignedPrice;
import com.guroomsoft.icms.biz.price.dto.ConsignedPriceReq;
import com.guroomsoft.icms.common.exception.CDatabaseException;
import com.guroomsoft.icms.common.exception.CInvalidArgumentException;
import com.guroomsoft.icms.common.exception.CRegistrationFailException;
import com.guroomsoft.icms.common.exception.CRemoveFailException;
import com.guroomsoft.icms.common.service.ExcelService;
import com.guroomsoft.icms.sap.JcoClient;
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
public class ConsignedPriceService {
    private static String RFC_NAME = "ZSD_ICMS_MAST_PR_SEND";   // 공급업체 사급단가 다운로드 RFC NAME
    private static String RFC_IN_PLANT = "I_WERKS";             // RFC IMPORT PARAM 회사코드
    private static String RFC_TABLE_NAME = "T_HEADER";          // RFC TABLE NAME

    private final ConsignedPriceDAO consignedPriceDAO;
    private final PlantDAO plantDAO;
    private final ExcelService excelService;

    private final JcoClient jcoClient;

    /**
     * 고객사 사급단가 목록 조회
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<ConsignedPrice> findConsignedPrice(ConsignedPriceReq cond) throws Exception
    {
        try {
            return consignedPriceDAO.selectConsignedPrice(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    public int getConsignedPriceCount(ConsignedPriceReq cond) throws Exception
    {
        try {
            return consignedPriceDAO.getConsignedPriceCount(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 고객사 사급단가 등록
     * @param data
     * @return
     */
    @Transactional
    public int createConsignedPrice(ConsignedPrice data) throws Exception
    {
        if (StringUtils.isBlank(data.getPlantCd()) || StringUtils.isBlank(data.getBgnValidDate())
                || StringUtils.isBlank(data.getBpCd()) || StringUtils.isBlank(data.getMatCd())
                || (data.getPriceUnit().intValue() == 0)
                || (data.getConsignedPrice().doubleValue() == Double.valueOf(0).doubleValue()) )
        {
            throw new CInvalidArgumentException();
        }

        try {
            return consignedPriceDAO.insertConsignedPrice(data);
        } catch (Exception e) {
            throw new CRegistrationFailException();
        }
    }

    /**
     * 고객사 사급단가 삭제
     * @param param
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeConsignedPrice(ConsignedPrice param) throws Exception
    {
        if (StringUtils.isBlank(param.getPlantCd()) || StringUtils.isBlank(param.getBgnValidDate()) )
        {
            throw new CInvalidArgumentException();
        }

        try {
            return consignedPriceDAO.deleteConsignedPrice(param);
        } catch (Exception e) {
            throw new CRemoveFailException();
        }
    }

    /**
     * 고객사 사급단가 병합처리
     * @param data
     * @return
     * @throws Exception
     */
    public int saveConsignedPrice(ConsignedPrice data) throws Exception
    {
        if (StringUtils.isBlank(data.getPlantCd()) || StringUtils.isBlank(data.getBgnValidDate())
                || StringUtils.isBlank(data.getBpCd()) || StringUtils.isBlank(data.getMatCd()) )
        {
            throw new CInvalidArgumentException();
        }

        try {
            return consignedPriceDAO.mergeConsignedPrice(data);
        } catch (Exception e) {
            throw new CRegistrationFailException();
        }
    }

    /**
     * 고객사 사급단가 엑셀 출력
     * @param fileName
     * @param reportTitle
     * @param pageHeader
     * @param columHeaders
     * @param rows
     * @return
     */
    public ResponseEntity<byte[]> exportToExcelForConsignedPrice(
            String fileName,
            String reportTitle,
            Map<String, Object> pageHeader,
            String[] columHeaders,
            List<ConsignedPrice> rows)
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
        for (ConsignedPrice data : rows)
        {
            List<Object> row = new ArrayList<Object>();
            row.add(String.valueOf(rowNum++));
            // 컬럼 순서에 해당하는 데이터를 채운다
            row.add( data.getPlantCd() );
            row.add( data.getPlantNm() );
            row.add( data.getBpCd() );
            row.add( data.getBpNm() );
            row.add( data.getMatCd() );
            row.add( data.getMatNm() );
            row.add( data.getConsignedPrice() );
            row.add( data.getPriceUnit() );
            row.add( data.getOriginConsignedPrice() );
            row.add( data.getCurUnit() );
            row.add( data.getSalesUnit() );
            row.add( data.getBgnValidDate() );
            row.add( data.getEndValidDate() );
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
     * SAP 다운로드 - 고객사 매입단가
     * @param plantCd
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    public int downloadConsignedPriceFromSap(String plantCd, Long reqUserUid) throws Exception
    {
        try {
            jcoClient.getFunction(RFC_NAME);
            LinkedHashMap<String, String> params = new LinkedHashMap<>();
            params.put(RFC_IN_PLANT, plantCd);          // 플랜트 코드
            jcoClient.setImportParam(params);
            jcoClient.runRunction();
            ArrayList<Map<String, Object>> dataSet = jcoClient.getTable(RFC_TABLE_NAME);

            // convert to object list
            ArrayList<ConsignedPrice> dataRows = convertToConsignedPrice(dataSet);

            // save DB
            return loadToDB(dataRows, reqUserUid);
        } catch (Exception e) {
            log.error("👉 Fail to get jco function");
            log.error(e.getMessage());
        }
        return 0;
    }

    /**
     * 고객사 사급단가 정보로 객체 변환
     * @param dataSet
     * @return
     */
    private ArrayList<ConsignedPrice> convertToConsignedPrice(ArrayList<Map<String, Object>> dataSet)
    {
        log.debug(">>>>>> convertToConsignedPrice");
        if (dataSet == null || dataSet.isEmpty()) {
            log.debug(">>>>>> convertToConsignedPrice :::: dataSet is null");
            return null;
        }

        ArrayList<ConsignedPrice> resultList = new ArrayList<>();
        for (Map<String, Object> dataItem : dataSet)
        {
            ConsignedPrice dataRow = new ConsignedPrice();
            dataRow.setPlantCd((String)dataItem.get("WERKS"));          // 플랜트 코드
            dataRow.setBpCd((String)dataItem.get("KUNNR"));             // 판매처 BP 코드
            dataRow.setMatCd((String)dataItem.get("MATNR"));            // 자재코드
            BigDecimal consignedPrice = BigDecimal.valueOf(Double.valueOf((String)dataItem.get("KBETR")));
            dataRow.setConsignedPrice(consignedPrice);     // 단가

            dataRow.setOriginConsignedPrice(BigDecimal.ZERO);
            dataRow.setCurUnit(StringUtils.defaultString((String)dataItem.get("KONWA")));      // 통화단위
            Integer priceUnit = Integer.valueOf((String)dataItem.get("KPEIN"));         // 가격결정단위
            dataRow.setPriceUnit(priceUnit);
            dataRow.setSalesUnit(StringUtils.defaultString((String)dataItem.get("KMEIN"))); // 출고단위
            dataRow.setBgnValidDate((String)dataItem.get("DATAB"));        // 효력시작일
            dataRow.setEndValidDate((String)dataItem.get("DATAI"));         // 효력종료일

            dataRow.setIfSeq((String)dataItem.get("ZIFSEQ"));       // 인터페이스 번호     S0000000000000000075
            dataRow.setIfType(StringUtils.defaultString((String)dataItem.get("ZIFTYPE")));     // 인터페이스 유형     TEST
            dataRow.setIfResult(StringUtils.defaultString((String)dataItem.get("ZIF_RESULT")));    // 메시지 유형       S : 성공 /  E : 오류
            dataRow.setIfMessage(StringUtils.defaultString((String)dataItem.get("ZIF_MESSAGE")));  // 메시지

            resultList.add(dataRow);
        }
        return resultList;
    }

    /**
     * 어드민수정 > 일반사용자도 수정
     * 일반사용자 수정 > 어드민계정에 대한 메뉴사라짐
     * 데이터 적재
     * @param dataRows
     * @param reqUserUid
     * @return
     */
    private int loadToDB(ArrayList<ConsignedPrice> dataRows, Long reqUserUid)
    {
        log.debug(">>>>> loadToDB");
        int totalUpdated = 0;

        for (ConsignedPrice item : dataRows)
        {
            int t = 0;
            try {
                item.setRegUid(reqUserUid);
                // 유효성 체크
                if (StringUtils.isBlank(item.getPlantCd()) || StringUtils.isBlank(item.getBgnValidDate())
                        || StringUtils.isBlank(item.getBpCd()) || StringUtils.isBlank(item.getMatCd()) )
                {
                    log.debug(">>>>> Skip ::: {} ::: {} ::: {} ::: {}", item.getPlantCd(), item.getMatCd(), item.getBpCd(), item.getBgnValidDate());
                    continue;
                }

                log.debug(item.toString());
                consignedPriceDAO.deleteConsignedPrice(item);
                int updated = consignedPriceDAO.insertConsignedPrice(item);
                if (updated > 0) {
                    totalUpdated++;
                }
            } catch (Exception e1) {
                log.error(e1.getMessage());
            }
        }
        return totalUpdated;
    }

    /**
     * 플랜트 목록 조회
     * @return
     */
    private List<Plant> getPlantList(String countryCd)
    {
        Plant cond = new Plant();
        if (StringUtils.isNotBlank(countryCd)) cond.setPlantCountry(countryCd);
        else cond.setPlantCountry("KR");

        cond.setUseAt(AppContant.CommonValue.YES.getValue());
        try {
            return plantDAO.selectPlant(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 전체 플랜트 고객사 사급 단가 다운로드
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    public int downloadConsignedPriceAll(Long reqUserUid) throws Exception
    {
        JcoClient jcoClient = null;

        int totalUpdated = 0;
        List<Plant> plantList = getPlantList("KR");
        if (plantList == null || plantList.isEmpty()) {
            return 0;
        }

        try {
            jcoClient.getFunction(RFC_NAME);
            for (Plant item : plantList)
            {
                try {
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put(RFC_IN_PLANT, item.getPlantCd());                    // 플랜트 코드
                    jcoClient.setImportParam(params);
                    jcoClient.runRunction();
                    ArrayList<Map<String, Object>> dataSet = jcoClient.getTable(RFC_TABLE_NAME);

                    // convert to object list
                    ArrayList<ConsignedPrice> dataRows = convertToConsignedPrice(dataSet);
                    // save DB
                    int updated = loadToDB(dataRows, reqUserUid);
                    if (updated > 0)
                    {
                        totalUpdated = totalUpdated + updated;
                    }
                } catch (Exception e1) {
                    log.error(e1.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("👉 Fail to get Consigned price jco function");
            log.error(e.getMessage());
        }
        return totalUpdated;
    }

}
