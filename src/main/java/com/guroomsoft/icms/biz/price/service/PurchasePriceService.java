package com.guroomsoft.icms.biz.price.service;

import com.guroomsoft.icms.biz.code.dao.PlantDAO;
import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.biz.price.dao.PurchasePriceDAO;
import com.guroomsoft.icms.biz.price.dto.PurchasePrice;
import com.guroomsoft.icms.biz.price.dto.PurchasePriceReq;
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
public class PurchasePriceService {
    private static String RFC_NAME = "ZMM_ICMS_EXPORT_INFO_S";  // 공급업체 사급단가 다운로드 RFC NAME
    private static String RFC_IN_SDATE = "I_SDATE";             // RFC IMPORT 유효일자 시작일 (옵션) 디폴트 오늘일자
    private static String RFC_IN_EDATE = "I_EDATE";             // RFC IMPORT 유효일자 종료일 (옵션)
    private static String RFC_IN_LIFNR = "I_LIFNR";             // RFC IMPORT 공급업체 BP코드(옵션)
    private static String RFC_IN_PLANTS = "I_WERKS";            // RFC IMPORT 플랜트 코드 목록

    private static String RFC_TABLE_NAME = "T_HEADER";          // RFC TABLE NAME

    private final PurchasePriceDAO purchasePriceDAO;
    private final PlantDAO plantDAO;

    private final ExcelService excelService;

    private final JcoClient jcoClient;

    /**
     * 공급업체 사급단가 목록 조회
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<PurchasePrice> findPurchasePrice(PurchasePriceReq cond)
    {
        try {
            return purchasePriceDAO.selectPurchasePrice(cond);
        } catch (Exception e) {
            log.error("{} findPurchasePrice {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 전체 레코드 카운트
     * @param cond
     * @return
     */
    public int getPurchasePriceCount(PurchasePriceReq cond) throws Exception
    {
        try {
            return purchasePriceDAO.selectPurchasePriceCount(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * 공급업체 사급단가 등록
     * @param data
     * @return
     */
    @Transactional
    public int createPurchasePrice(PurchasePrice data) throws Exception
    {
        if (StringUtils.isBlank(data.getPlantCd()) || StringUtils.isBlank(data.getBpCd())
                || StringUtils.isBlank(data.getMatCd()) || (data.getPriceUnit().intValue() == 0)
                || (data.getPurPrice().doubleValue() == Double.valueOf(0).doubleValue()) )
        {
            throw new CInvalidArgumentException();
        }

        try {
            return purchasePriceDAO.insertPurchasePrice(data);
        } catch (Exception e) {
            throw new CRegistrationFailException();
        }
    }

    /**
     * 공급업체 사급단가 삭제
     * @param param
     * @return
     * @throws Exception
     */
    @Transactional
    public int removePurchasePrice(PurchasePrice param) throws Exception
    {
        if (StringUtils.isBlank(param.getPlantCd()) )
        {
            throw new CInvalidArgumentException();
        }

        try {
            return purchasePriceDAO.deletePurchasePrice(param);
        } catch (Exception e) {
            throw new CRemoveFailException();
        }
    }

    /**
     * 공급업체 사급단가 병합처리
     * @param data
     * @return
     * @throws Exception
     */
    public int savePurchasePrice(PurchasePrice data) throws Exception
    {
        if (StringUtils.isBlank(data.getPlantCd())  || StringUtils.isBlank(data.getBpCd())
                || StringUtils.isBlank(data.getMatCd()) )
        {
            throw new CInvalidArgumentException();
        }

        try {
            return purchasePriceDAO.mergePurchasePrice(data);
        } catch (Exception e) {
            throw new CRegistrationFailException();
        }
    }

    /**
     * 공급사 사급단가 엑셀 출력
     * @param fileName
     * @param reportTitle
     * @param pageHeader
     * @param columHeaders
     * @param rows
     * @return
     */
    public ResponseEntity<byte[]> exportToExcelForPurchasePrice(
            String fileName,
            String reportTitle,
            Map<String, Object> pageHeader,
            String[] columHeaders,
            List<PurchasePrice> rows)
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
        for (PurchasePrice data : rows)
        {
            List<Object> row = new ArrayList<Object>();
            row.add(String.valueOf(rowNum++));
            // 컬럼 순서에 해당하는 데이터를 채운다
            row.add( data.getPurOrg() );
            row.add( data.getPlantCd() );
            row.add( data.getPlantNm() );
            row.add( data.getBpCd() );
            row.add( data.getBpNm() );
            row.add( data.getMatCd() );
            row.add( data.getMatNm() );
            row.add( data.getPurPrice() );
            row.add( data.getPriceUnit() );
            row.add( data.getOriginPurPrice() );
            row.add( data.getCurUnit() );
            row.add( data.getPurchaseUnit() );
            row.add( data.getPriceStatus() );
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
     * SAP 다운로드 - 공급업체 매입단가
     * @param params
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    public int downloadPurchasePriceFromSap(LinkedHashMap<String, Object> params, Long reqUserUid) throws Exception
    {
        try {
            jcoClient.getFunction(RFC_NAME);
            LinkedHashMap<String, Object> impParams = new LinkedHashMap<>();
            if (params.containsKey("plants")) {
                impParams.put(RFC_IN_PLANTS, params.get("plants"));        // 공급업체코드
            }

            if (params.containsKey("bpCd")) {
                impParams.put(RFC_IN_LIFNR, params.get("bpCd"));        // 공급업체코드
            }

            log.info(impParams.toString());
            jcoClient.setImportParam(impParams);
            jcoClient.runRunction();
            ArrayList<Map<String, Object>> dataSet = jcoClient.getTable(RFC_TABLE_NAME);

            // convert to object list
            ArrayList<PurchasePrice> dataRows = convertToPurchasePrice(dataSet);

            // save DB
            return loadToDB(dataRows, reqUserUid);
        } catch (Exception e) {
            log.error("👉 Fail to get jco function");
            log.error(e.getMessage());
        }
        return 0;
    }


    /**
     * 구매단가 정보로 객체 변환
     * @param dataSet
     * @return
     */
    private ArrayList<PurchasePrice> convertToPurchasePrice(ArrayList<Map<String, Object>> dataSet)
    {
        if (dataSet == null || dataSet.isEmpty()) {
            return null;
        }

        ArrayList<PurchasePrice> resultList = new ArrayList<>();
        for (Map<String, Object> dataItem : dataSet)
        {
            PurchasePrice dataRow = new PurchasePrice();
            dataRow.setIfSeq((String)dataItem.get("ZIFSEQ"));                                   // 인터페이스 번호     S0000000000000000075
            dataRow.setIfType(StringUtils.defaultString((String)dataItem.get("ZIFTYPE")));      // 인터페이스 유형     TEST

            dataRow.setPurOrg((String)dataItem.get("EKORG"));       // 구매조직 1000 내수 1100 외자
            dataRow.setPlantCd((String)dataItem.get("WERKS"));      // 플랜트 코드
            dataRow.setBpCd((String)dataItem.get("LIFNR"));         // 공급업체 BP 코드
            dataRow.setMatCd((String)dataItem.get("MATNR"));        // 자재번호
            dataRow.setCurUnit(StringUtils.defaultString((String)dataItem.get("WAERS")));      // 통화단위

            BigDecimal purchasePrice = BigDecimal.valueOf(Double.valueOf((String)dataItem.get("KBETR")));
            Integer priceUnit = Integer.valueOf((String)dataItem.get("PEINH"));     // 가격단위
            dataRow.setPriceUnit(priceUnit);        // 가격결정단위
            dataRow.setPurPrice(purchasePrice);     // 구매단가
            dataRow.setOriginPurPrice(BigDecimal.ZERO);
            dataRow.setPurchaseUnit(StringUtils.defaultString((String)dataItem.get("BSTME")));   // 오더단위
            dataRow.setPriceStatus(StringUtils.defaultString((String)dataItem.get("KZUST"))); // 단가상태 10 정단가, 20 가단가
            dataRow.setBgnValidDate((String)dataItem.get("DATAB"));    // 효력시작일

            dataRow.setIfResult(StringUtils.defaultString((String)dataItem.get("ZIF_RESULT")));    // 메시지 유형       S : 성공 /  E : 오류
            dataRow.setIfMessage(StringUtils.defaultString((String)dataItem.get("ZIF_MESSAGE")));  // 메시지

            resultList.add(dataRow);
        }
        return resultList;
    }

    /**
     * 데이터 적재
     * @param dataRows
     * @param reqUserUid
     * @return
     */
    private int loadToDB(ArrayList<PurchasePrice> dataRows, Long reqUserUid)
    {
        int totalUpdated = 0;
        Long userUid = 1L;
        if (reqUserUid != null) {
            userUid = reqUserUid;
        }

        for (PurchasePrice item : dataRows)
        {
            int t = 0;
            try {
                item.setRegUid(userUid);
                // 유효성 체크
                if (StringUtils.isBlank(item.getPlantCd()) || StringUtils.isBlank(item.getBpCd())
                        || StringUtils.isBlank(item.getMatCd()) )
                {
                    continue;
                }

                log.debug(item.toString());
                purchasePriceDAO.deletePurchasePrice(item);
                int updated = purchasePriceDAO.insertPurchasePrice(item);
                if (updated > 0) {
                    totalUpdated++;
                }
            } catch (Exception e1) {
                log.error("👉 fail to save {}", item);
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
     * 전체 플랜트 공급 협력사 사급 단가 다운로드
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    public int downloadPurchasePriceAll(Long reqUserUid) throws Exception
    {
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
//                    params.put(RFC_IN_PARAM, item.getPlantCd());   // 플랜트 코드
//                    jcoClient.setImportParam(params);
                    jcoClient.runRunction();
                    ArrayList<Map<String, Object>> dataSet = jcoClient.getTable(RFC_TABLE_NAME);

                    // convert to object list
                    ArrayList<PurchasePrice> dataRows = convertToPurchasePrice(dataSet);
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
            log.error("👉 Fail to get Purchase price jco function");
            log.error(e.getMessage());
        }
        return totalUpdated;
    }


    public String getExcelContent(int oid)
    {
        return purchasePriceDAO.selectExcelContent(oid);
    }

}
