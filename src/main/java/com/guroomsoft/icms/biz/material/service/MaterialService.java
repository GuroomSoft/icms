package com.guroomsoft.icms.biz.material.service;

import com.guroomsoft.icms.biz.material.dao.MaterialDAO;
import com.guroomsoft.icms.biz.material.dto.Material;
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
public class MaterialService {
    private final MaterialDAO materialDAO;
    private final ExcelService excelService;
    /**
     * 재질 마스터 목록
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<Material> findMaterial(Material cond) throws Exception {
        try {
            return materialDAO.selectMaterial(cond);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CDatabaseException();
        }
    }

    /**
     * Export to excel
     * @param fileName
     * @param reportTitle
     * @param pageHeader
     * @param columHeaders
     * @param rows
     * @return
     */
    public ResponseEntity<byte[]> exportToExcelForMaterial(
            String fileName,
            String reportTitle,
            Map<String, Object> pageHeader,
            String[] columHeaders,
            List<Material> rows)
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
        for (Material data : rows)
        {
            List<Object> row = new ArrayList<Object>();
            row.add(String.valueOf(rowNum++));
            // 컬럼 순서에 해당하는 데이터를 채운다
            row.add( data.getRawMaterialCd() );
            row.add( data.getRawMaterialNm() );
            row.add( data.getSteelGrade() );
            row.add( data.getMaterialCd() );
            row.add( data.getMaterialNm() );
            row.add( data.getCustomerMatCd() );
            row.add( data.getRemark() );
            row.add( data.getUseAtNm() );
            row.add( data.getRegDt() );
            row.add( data.getAccountNm() );

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
     * 재질 마스터 상세조회
     * @param materialCd
     * @return
     */
    @Transactional(readOnly = true)
    public Material findMaterialByKey(String materialCd, String materialNm)  throws Exception
    {
        if (StringUtils.isBlank(materialCd) || StringUtils.isBlank(materialNm)) {
            throw new CInvalidArgumentException();
        }

        try {
            return materialDAO.selectMaterialByKey(materialCd, materialNm);
        } catch (Exception e) {
            throw new CDatabaseException();
        }
    }

    /**
     * 재질 마스터 등록
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public Material createMaterial(Material data, Long reqUserUid) throws Exception
    {
        if (!isValid(data)) {
            throw new CInvalidArgumentException();
        }

        if (isExist(data)) {
            throw new CAlreadyExistException();
        }

        // 디폴트 값 설정
        if (StringUtils.isBlank(data.getUseAt())) data.setUseAt(AppContant.CommonValue.YES.getValue());

        int inserted = 0;
        try {
            data.setRegUid(reqUserUid);
            inserted = materialDAO.insertMaterial(data);
            if (inserted == 0) {
                return null;
            } else {
                return materialDAO.selectMaterialByKey(data.getMaterialCd(), data.getMaterialNm());
            }
        } catch (Exception e) {
            throw new CRegistrationFailException();
        }
    }

    /**
     * 재질 마스터 다중 등록
     * @param dataList
     * @return
     * @throws Exception
     */
    public int saveMultipleMaterial(List<Material> dataList, Long reqUserUid) throws Exception
    {
        if (dataList == null || dataList.isEmpty())
        {
            throw new CInvalidArgumentException();
        }

        int totalCount = 0;
        for (Material item : dataList) {
            if (StringUtils.isBlank(item.getMaterialCd()) || StringUtils.isBlank(item.getMaterialNm())) {
                continue;
            }

            if (StringUtils.isBlank(item.getUseAt()))
            {
                item.setRegUid(reqUserUid);
                item.setUseAt(AppContant.CommonValue.YES.getValue());
            }

            int updated = 0;
            try {
                materialDAO.deleteMaterial(item.getMaterialCd(), item.getMaterialNm());
                updated = materialDAO.insertMaterial(item);
                if (updated > 0)
                {
                    totalCount++;
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        return totalCount;
    }


    /**
     * 재질 마스터 저장처리
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int saveMaterial(Material data) throws Exception
    {
        if (data == null || StringUtils.isBlank(data.getMaterialCd()))
        {
            throw new CInvalidArgumentException();
        }

        if (StringUtils.isBlank(data.getUseAt())) data.setUseAt(AppContant.CommonValue.YES.getValue());

        try {
            return materialDAO.saveMaterial(data);
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }

    /**
     * 유효성 검사
     * @param data
     * @return
     */
    private boolean isValid(Material data)
    {
        if (data == null
                || StringUtils.isBlank(data.getMaterialCd())
                || StringUtils.isBlank(data.getMaterialNm())
                || StringUtils.isBlank(data.getRawMaterialCd()) )
        {
            return false;
        }

        return true;
    }

    /**
     * 중복체크
     * @param data
     * @return
     */
    private boolean isExist(Material data) throws CInvalidArgumentException
    {
        if (data == null || StringUtils.isBlank(data.getMaterialCd()) || StringUtils.isBlank(data.getMaterialNm()))
        {
            throw new CInvalidArgumentException();
        }
        try {
            Material d = materialDAO.selectMaterialByKey(data.getMaterialCd(), data.getMaterialNm());
            if (d != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * Material 정보 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyMaterial(Material data, Long reqUserUid) throws Exception
    {
        if (data == null || StringUtils.isBlank(data.getMaterialCd()) || StringUtils.isBlank(data.getMaterialNm()) )
        {
            throw new CInvalidArgumentException();
        }
        try {
            data.setModUid(reqUserUid);
            return materialDAO.updateMaterial(data);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CModifictionFailException();
        }
    }

    /**
     * Material 정보 삭제
     * @param materialCd    재질코드
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeMaterial(String materialCd, String materialNm)  throws Exception
    {
        if (StringUtils.isBlank(materialCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            return materialDAO.deleteMaterial(materialCd, materialNm);
        } catch (Exception e) {
            throw new CRemoveFailException();
        }
    }

}
