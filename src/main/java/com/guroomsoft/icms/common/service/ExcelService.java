package com.guroomsoft.icms.common.service;

import com.guroomsoft.icms.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class ExcelService {
    private XSSFCellStyle titleStyle;
    private XSSFCellStyle headerStyle;
    private XSSFCellStyle dataHeaderStyle;
    private XSSFCellStyle dataCenterStyle;
    private XSSFCellStyle dataRightStyle;

    /**
     * 폰트 생성
     * @param wb
     * @param fontName
     * @param fontSize
     * @param isBold
     * @return
     */
    private Font createExcelFont(XSSFWorkbook wb, String fontName, short fontSize, boolean isBold)
    {
        if (wb == null) {
            return null;
        } else if (StringUtils.isEmpty(fontName)) {
            return null;
        }

        Font font = wb.createFont();
        font.setFontName(fontName);
        font.setBold(isBold);
        font.setFontHeightInPoints(fontSize);

        return font;
    }

    /**
     * 엑셀파일 타이틀 설정
     * @param wb
     * @return
     */
    private XSSFCellStyle createExcelTitleStyle(XSSFWorkbook wb)
    {
        if (wb == null) {
            return null;
        }

//        if (titleStyle == null) {
            titleStyle = wb.createCellStyle();
//        }

        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = createExcelFont(wb, "맑은 고딕", (short)20, true);
        titleStyle.setFont(font);

        return titleStyle;
    }

    /**
     * Header 스타일
     * @param wb
     * @return
     */
    private XSSFCellStyle createExcelHeaderStyle(XSSFWorkbook wb)
    {
        if (wb == null) {
            return null;
        }

//        if (headerStyle == null) {
            headerStyle = wb.createCellStyle();
//        }

        headerStyle.setAlignment(HorizontalAlignment.LEFT);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Font font = createExcelFont(wb, "맑은 고딕", (short)10, true);
        headerStyle.setFont(font);

        return headerStyle;
    }

    /**
     * 데이터 타이틀 스타일
     * @param wb
     * @return
     */
    private XSSFCellStyle createExcelDataTitleStyle(XSSFWorkbook wb)
    {
        if (wb == null) {
            return null;
        }

//        if (dataHeaderStyle == null) {
            dataHeaderStyle = wb.createCellStyle();
//        }

        dataHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        dataHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 테두리선 그리기
        dataHeaderStyle.setBorderTop(BorderStyle.THIN);
        dataHeaderStyle.setBorderBottom(BorderStyle.THIN);
        dataHeaderStyle.setBorderLeft(BorderStyle.THIN);
        dataHeaderStyle.setBorderRight(BorderStyle.THIN);

        Font font = createExcelFont(wb, "맑은 고딕", (short)10, true);
        dataHeaderStyle.setFont(font);

        return dataHeaderStyle;
    }

    /**
     * 데이터 스타일
     * @param wb
     * @return
     */
    private XSSFCellStyle createExcelDataCenterStyle(XSSFWorkbook wb )
    {
        if (wb == null) {
            return null;
        }

        dataCenterStyle = wb.createCellStyle();

        dataCenterStyle.setAlignment(HorizontalAlignment.CENTER);
        dataCenterStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 테두리선 그리기
        dataCenterStyle.setBorderBottom(BorderStyle.DOTTED);

        Font font = createExcelFont(wb, "맑은 고딕", (short)10, false);
        dataCenterStyle.setFont(font);

        return dataCenterStyle;
    }

    private XSSFCellStyle createExcelDataRightStyle(XSSFWorkbook wb )
    {
        if (wb == null) {
            return null;
        }

        dataRightStyle = wb.createCellStyle();

        dataRightStyle.setAlignment(HorizontalAlignment.RIGHT);
        dataRightStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.###"));

        dataRightStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // 테두리선 그리기
        dataRightStyle.setBorderBottom(BorderStyle.DOTTED);

        Font font = createExcelFont(wb, "맑은 고딕", (short)10, false);
        dataRightStyle.setFont(font);

        return dataRightStyle;
    }

    /**
     * Excel 파일 다운로드를 위한 HttpHeader 설정
     * @param fileName
     * @return
     * @throws UnsupportedEncodingException
     */
    public HttpHeaders createHttpHeaders(String fileName) throws UnsupportedEncodingException
    {
        // 1. Header 영역 설정
        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // 파일명
        String encodingExcelFileName = URLEncoder.encode(fileName, "UTF-8");

        // 전송헤더에 파일명 세팅
        httpHeader.setContentDispositionFormData("attachment", encodingExcelFileName);
        httpHeader.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return httpHeader;
    }
    /**
     * 엑셀 내보내기
     * @param sheetMap Null인 경우 No Content로 반환
     * @return
     * @throws IOException
     */
    public ResponseEntity<byte[]> exportToExcel(String excelFileName, Map<String, Object> sheetMap) throws Exception
    {
        // 1. Header 영역 설정
        HttpHeaders httpHeader = new HttpHeaders();
        httpHeader.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        // Excel 파일명 정의
        if (StringUtils.isBlank(excelFileName)) {
            excelFileName = DateTimeUtil.currentDatetime2() + ".xlsx";
        }

        String encodingExcelFileName = URLEncoder.encode(excelFileName, "UTF-8");
        // 전송헤더에 파일명 세팅
        httpHeader.setContentDispositionFormData("attachment", encodingExcelFileName);
        httpHeader.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        if (sheetMap == null) {
            return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
        }

        int rowNum = 0;
        SXSSFWorkbook wb = new SXSSFWorkbook(-1);


        /* 2. 새 Excel Workbook 생성 */
        Set<String> sheets = sheetMap.keySet();
        for(String sheetSrc: sheets) {
            Map<String, Object> sheetDef = (Map<String, Object>) sheetMap.get(sheetSrc);

            // 컬럼 정의
            List<String> colTitles = new ArrayList<String>();
            if (sheetDef.containsKey("colTitle"))
            {
                colTitles = (List<String>) sheetDef.get("colTitle");
            }


            /* 3. 새로운 Sheet 생성 */
            // Sheet name
            Sheet sheet = null;
            if (sheetDef.containsKey("sheetName")) {
                sheet = wb.createSheet((String)sheetDef.get("sheetName"));
            } else {
                sheet = wb.createSheet(sheetSrc);
            }

            this.titleStyle = createExcelTitleStyle(wb.getXSSFWorkbook());
            this.headerStyle = createExcelHeaderStyle(wb.getXSSFWorkbook());
            this.dataHeaderStyle = createExcelDataTitleStyle(wb.getXSSFWorkbook());
            this.dataCenterStyle = createExcelDataCenterStyle(wb.getXSSFWorkbook());
            this.dataRightStyle = createExcelDataRightStyle(wb.getXSSFWorkbook());

            if (sheet == null) {
                break;
            }

            /*
             * 4. 새로운 Sheet에 Row를 만든 후 해당 Row의 각 Cell에 값을 입력
             * 보고서 제목 설정
             */
            if (sheetDef.containsKey("reportTitle"))
            {
                sheet.addMergedRegion(new CellRangeAddress(0,0,0,colTitles.size()-1)); //열시작, 열종료, 행시작, 행종료 (자바배열과 같이 0부터 시작)
                Row titleRow = sheet.createRow(rowNum++);  //
                Cell cell1 = titleRow.createCell(0);
                cell1.setCellValue((String) sheetDef.get("reportTitle"));
                cell1.setCellStyle(titleStyle);
            }

            /* 5. 조회 조건 설정 영역 */
            if (sheetDef.containsKey("searchCondition"))
            {
                Map<String, Object> headerConts = (Map<String, Object>) sheetDef.get("searchCondition");
                if (headerConts != null) {
                    for (int i = 0; i < headerConts.size(); i++)
                    {
                        Row headerRow = sheet.createRow(rowNum++);
                        Cell cell = headerRow.createCell(0);
                        cell.setCellValue((String)headerConts.get("cond" + String.valueOf(i)));
                        cell.setCellStyle(headerStyle);
                    }
                }
            }

            /* 6. 컬럼 타이틀 설정 영역 */
            if ( (colTitles != null) || !colTitles.isEmpty())
            {
                Row columnHeaderRow = sheet.createRow(rowNum++);


                for(int i=0; i<colTitles.size(); i++)
                {
                    Cell cell = columnHeaderRow.createCell(i);
                    cell.setCellValue(colTitles.get(i));
                    cell.setCellStyle(dataHeaderStyle);
                    sheet.setColumnWidth(i, 3000);
                }
            }

            /* 7. 데이터 설정 영역 */
            List<List<Object>> datas = (List<List<Object>>) sheetDef.get("rowData");
            for(List<Object> data: datas)
            {
                Row dataRow = sheet.createRow(rowNum++);
                int colIdx = 0;
                for(Object col: data)
                {
                    Cell cell = dataRow.createCell(colIdx++);
                    if (col == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(dataCenterStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "String")) {
                        cell.setCellValue((String)col);
                        cell.setCellStyle(dataCenterStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Long")) {
                        cell.setCellValue((Long)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Integer")) {
                        cell.setCellValue((Integer)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Float")) {
                        cell.setCellValue((Float)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "BigDecimal")) {
                        cell.setCellValue(((BigDecimal)col).longValue());
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Double")) {
                        cell.setCellValue((Double)col);
                        cell.setCellStyle(dataRightStyle);
                    }
                }
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write( bos );
            byte[] contents = bos.toByteArray();
            return new ResponseEntity<byte[]>(contents, httpHeader, HttpStatus.OK);
        } catch (FileNotFoundException e) {
            log.error("엑셀출력 에러 ::: FileNotFoundException");
            return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            log.error("엑셀출력 에러 ::: IOException");
            return new ResponseEntity<byte[]>(null, httpHeader, HttpStatus.NO_CONTENT);
        } finally {
            wb.close();
            bos.close();
        }
    }

    /**
     * 엑셀 내보내기
     * @param sheetMap Null인 경우 No Content로 반환
     * @return
     * @throws IOException
     */
    public byte[] exportToExcel2(String excelFileName, Map<String, Object> sheetMap) throws Exception
    {
        // Excel 파일명 정의
        if (StringUtils.isBlank(excelFileName)) {
            excelFileName = DateTimeUtil.currentDatetime2() + ".xlsx";
        }

        if (sheetMap == null) {
            return null;
        }
        int rowNum = 0;

        /* 2. 새 Excel Workbook 생성 */
        SXSSFWorkbook wb = new SXSSFWorkbook(-1);


        Set<String> sheets = sheetMap.keySet();
        for(String sheetSrc: sheets) {
            Map<String, Object> sheetDef = (Map<String, Object>) sheetMap.get(sheetSrc);

            // 컬럼 정의
            List<String> colTitles = new ArrayList<String>();
            if (sheetDef.containsKey("colTitle"))
            {
                colTitles = (List<String>) sheetDef.get("colTitle");
            }


            /* 3. 새로운 Sheet 생성 */
            // Sheet name
            Sheet sheet = null;
            if (sheetDef.containsKey("sheetName")) {
                sheet = wb.createSheet((String)sheetDef.get("sheetName"));
            } else {
                sheet = wb.createSheet(sheetSrc);
            }

            this.titleStyle = createExcelTitleStyle(wb.getXSSFWorkbook());
            this.headerStyle = createExcelHeaderStyle(wb.getXSSFWorkbook());
            this.dataHeaderStyle = createExcelDataTitleStyle(wb.getXSSFWorkbook());
            this.dataCenterStyle = createExcelDataCenterStyle(wb.getXSSFWorkbook());
            this.dataRightStyle = createExcelDataRightStyle(wb.getXSSFWorkbook());


            if (sheet == null) {
                break;
            }

            /*
             * 4. 새로운 Sheet에 Row를 만든 후 해당 Row의 각 Cell에 값을 입력
             * 보고서 제목 설정
             */
            if (sheetDef.containsKey("reportTitle"))
            {
                sheet.addMergedRegion(new CellRangeAddress(0,0,0,colTitles.size()-1)); //열시작, 열종료, 행시작, 행종료 (자바배열과 같이 0부터 시작)
                Row titleRow = sheet.createRow(rowNum++);  //
                Cell cell1 = titleRow.createCell(0);
                cell1.setCellValue((String) sheetDef.get("reportTitle"));
                cell1.setCellStyle(titleStyle);
            }

            /* 5. 조회 조건 설정 영역 */
            if (sheetDef.containsKey("searchCondition"))
            {
                Map<String, Object> headerConts = (Map<String, Object>) sheetDef.get("searchCondition");
                if (headerConts != null) {
                    for (int i = 0; i < headerConts.size(); i++)
                    {
                        Row headerRow = sheet.createRow(rowNum++);
                        Cell cell = headerRow.createCell(0);
                        cell.setCellValue((String)headerConts.get("cond" + String.valueOf(i)));
                        cell.setCellStyle(headerStyle);
                    }
                }
            }

            /* 6. 컬럼 타이틀 설정 영역 */
            if ( (colTitles != null) || !colTitles.isEmpty())
            {
                Row columnHeaderRow = sheet.createRow(rowNum++);
                for(int i=0; i<colTitles.size(); i++)
                {
                    Cell cell = columnHeaderRow.createCell(i);
                    cell.setCellValue(colTitles.get(i));
                    cell.setCellStyle(dataHeaderStyle);
                    sheet.setColumnWidth(i, 3000);
                }
            }

            /* 7. 데이터 설정 영역 */
            List<List<Object>> datas = (List<List<Object>>) sheetDef.get("rowData");
            for(List<Object> data: datas)
            {
                Row dataRow = sheet.createRow(rowNum++);
                int colIdx = 0;
                for(Object col: data)
                {
                    Cell cell = dataRow.createCell(colIdx++);
                    if (col == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(dataCenterStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "String")) {
                        cell.setCellValue((String)col);
                        cell.setCellStyle(dataCenterStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Long")) {
                        cell.setCellValue((Long)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Integer")) {
                        cell.setCellValue((Integer)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Float")) {
                        cell.setCellValue((Float)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "BigDecimal")) {
                        //cell.setCellValue(((BigDecimal)col).longValue());
                        cell.setCellValue(((BigDecimal) col).doubleValue());
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Double")) {
                        cell.setCellValue((Double)col);
                        cell.setCellStyle(dataRightStyle);
                    }
                }
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write( bos );
            byte[] contents = bos.toByteArray();
            return contents;
        } catch (FileNotFoundException e) {
            log.error("엑셀출력 에러 ::: FileNotFoundException");
            return null;
        } catch (IOException e) {
            log.error("엑셀출력 에러 ::: IOException");
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            wb.close();
            bos.close();
        }
    }


    /**
     * 엑셀 내보내기
     * @param sheetMap Null인 경우 No Content로 반환
     * @return
     * @throws IOException
     */
    public byte[] exportToExcelByTable(String excelFileName, Map<String, Object> sheetMap) throws Exception
    {
        // Excel 파일명 정의
        if (StringUtils.isBlank(excelFileName)) {
            excelFileName = DateTimeUtil.currentDatetime2() + ".xlsx";
        }

        if (sheetMap == null) {
            return null;
        }
        int rowNum = 0;

        /* 2. 새 Excel Workbook 생성 */
        SXSSFWorkbook wb = new SXSSFWorkbook(-1);


        Set<String> sheets = sheetMap.keySet();
        for(String sheetSrc: sheets) {
            Map<String, Object> sheetDef = (Map<String, Object>) sheetMap.get(sheetSrc);

            // 컬럼 정의
            List<String> colTitles = new ArrayList<String>();
            if (sheetDef.containsKey("colTitle"))
            {
                colTitles = (List<String>) sheetDef.get("colTitle");
            }


            /* 3. 새로운 Sheet 생성 */
            // Sheet name
            Sheet sheet = null;
            if (sheetDef.containsKey("sheetName")) {
                sheet = wb.createSheet((String)sheetDef.get("sheetName"));
            } else {
                sheet = wb.createSheet(sheetSrc);
            }

            this.dataHeaderStyle = createExcelDataTitleStyle(wb.getXSSFWorkbook());
            this.dataCenterStyle = createExcelDataCenterStyle(wb.getXSSFWorkbook());
            this.dataRightStyle = createExcelDataRightStyle(wb.getXSSFWorkbook());


            if (sheet == null) {
                break;
            }

            /* 6. 컬럼 타이틀 설정 영역 */
            if ( (colTitles != null) || !colTitles.isEmpty())
            {
                Row columnHeaderRow = sheet.createRow(rowNum++);
                for(int i=0; i<colTitles.size(); i++)
                {
                    Cell cell = columnHeaderRow.createCell(i);
                    cell.setCellValue(colTitles.get(i));
                    cell.setCellStyle(dataHeaderStyle);
                    sheet.setColumnWidth(i, 3000);
                }
            }

            /* 7. 데이터 설정 영역 */
            List<List<Object>> datas = (List<List<Object>>) sheetDef.get("rowData");
            for(List<Object> data: datas)
            {
                Row dataRow = sheet.createRow(rowNum++);
                int colIdx = 0;
                for(Object col: data)
                {
                    Cell cell = dataRow.createCell(colIdx++);
                    if (col == null) {
                        cell.setCellValue("");
                        cell.setCellStyle(dataCenterStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "String")) {
                        cell.setCellValue((String)col);
                        cell.setCellStyle(dataCenterStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Long")) {
                        cell.setCellValue((Long)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Integer")) {
                        cell.setCellValue((Integer)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Float")) {
                        cell.setCellValue((Float)col);
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "BigDecimal")) {
                        cell.setCellValue(((BigDecimal)col).longValue());
                        cell.setCellStyle(dataRightStyle);
                    } else if (StringUtils.equals(col.getClass().getSimpleName(), "Double")) {
                        cell.setCellValue((Double)col);
                        cell.setCellStyle(dataRightStyle);
                    }
                }
            }
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            wb.write( bos );
            byte[] contents = bos.toByteArray();
            return contents;
        } catch (FileNotFoundException e) {
            log.error("엑셀출력 에러 ::: FileNotFoundException");
            return null;
        } catch (IOException e) {
            log.error("엑셀출력 에러 ::: IOException");
            return null;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        } finally {
            wb.close();
            bos.close();
        }
    }
}
