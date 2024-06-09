package com.guroomsoft.icms.biz.material.controller;

import com.guroomsoft.icms.biz.material.dto.AnnouncePrice;
import com.guroomsoft.icms.biz.material.dto.AnnouncePriceDetail;
import com.guroomsoft.icms.biz.material.service.AnnouncePriceService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "공시단가 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/announcePrice")
public class AnnouncePriceController {
    private final ResponseService responseService;
    private final AnnouncePriceService announcePriceService;

    @Operation(summary = "공시단가 문서 목록 조회", description = "공시단가 문서 목록 조회")
    @RequestMapping(value = "/doc", method = {RequestMethod.GET})
    public ListResult<AnnouncePrice> findAnnouncePriceDoc(
            @Parameter(description = "국가코드(C08)") @RequestParam(required = false) String countryCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "문서상태(C09)") @RequestParam(required = false) String docStatus,
            @Parameter(description = "조회 시작일") @RequestParam(required = false) String fromDate,
            @Parameter(description = "조회 종료일") @RequestParam(required = false) String toDate)

    {
        Map<String, String> cond = new HashMap<>();

        if (StringUtils.isNotBlank(countryCd)) cond.put("countryCd", countryCd);
        if (StringUtils.isNotBlank(searchWord)) cond.put("docTitle", searchWord);
        if (StringUtils.isNotBlank(docStatus)) cond.put("docStatus", docStatus);
        if (StringUtils.isNotBlank(fromDate)) cond.put("fromDate", fromDate);
        if (StringUtils.isNotBlank(toDate)) cond.put("toDate", toDate);

        List<AnnouncePrice> resultSet = null;
        try {
            resultSet = announcePriceService.findAnnouncePrice(cond);

            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공시단가 문서 상세 조회", description = "공시단가 문서 상세 조회")
    @RequestMapping(value = "/doc/{docNo}", method = {RequestMethod.GET})
    public DataResult<AnnouncePrice> findAnnouncePriceDocByKey(
            @Parameter(description = "문서번호", required = true) @PathVariable String docNo)
    {
        try {
            AnnouncePrice resultSet = announcePriceService.findAnnouncePriceByKey(docNo);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "공시단가 문서의 가격 리스트 조회", description = "공시단가 문서의 가격 목록 조회")
    @RequestMapping(value = "/doc/{docNo}/details", method = {RequestMethod.GET})
    public ListResult<AnnouncePriceDetail> findAnnouncePriceListByDocNo(
            @Parameter(description = "문서번호", required = true) @PathVariable String docNo)
    {
        try {
            AnnouncePriceDetail cond = new AnnouncePriceDetail();
            cond.setDocNo(docNo);
            List<AnnouncePriceDetail> resultSet = announcePriceService.findAnnouncePriceDtl(cond);
            return responseService.getListResult(resultSet);
        } catch (CInvalidArgumentException e) {
            throw new CInvalidArgumentException();
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }


    @Operation(summary = "공시단가 문서 상세 엑셀 내보내기", description = "공시단가 문서 상세 엑셀 내보내기")
    @RequestMapping(value = "/doc/{docNo}/exportToExcel", method = {RequestMethod.GET})
    public ResponseEntity<byte[]> findExportToExcelForAnnounceDetail(
            @Parameter(description = "문서번호", required = true) @PathVariable String docNo)
    {
        try {
            String reportTitle = "원소재 공시단가";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());
            AnnouncePrice resultSet = announcePriceService.findAnnouncePriceByKey(docNo);

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("cond0", String.format("[문서] : (%s) %s", resultSet.getDocNo(), resultSet.getDocTitle() ));
            condMap.put("cond1", String.format("[작성일시] : %s", resultSet.getWriterDt() ));
            condMap.put("cond2", String.format("[작성자] : (%s) %s", resultSet.getWriterId(), resultSet.getWriterNm() ));
            condMap.put("cond3", String.format("[국가] : (%s) %s", resultSet.getCountryCd(), resultSet.getCountryNm() ));

            String[] colHeaders = {"No", "원소재코드", "원소재명", "유형(강종)", "재질코드", "재질명", "사급재질단가", "Scrap 단가",
                    "비고", "등록일시", "등록자", "수정일시", "수정자"};

            return announcePriceService.exportToExcelForAnnouncePriceDetail(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultSet.getDetails());
        } catch (CInvalidArgumentException e) {
            throw new CInvalidArgumentException();
        } catch (CNotFoundException e) {
            throw new CNotFoundException();
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공시단가 문서 일괄 등록", description = "업로드 양식의 원본 값과 Json을 파싱한 결과를 동시에 업로드 한 공시단가 문서 등록처리")
    @RequestMapping(value = "/doc/registration", method = {RequestMethod.POST})
    public DataResult<AnnouncePrice> registrationAnnouncePriceDoc(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePrice data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            AnnouncePrice newDoc = announcePriceService.createAnnouncePriceDoc(data, Long.valueOf(reqUserUid));
            return responseService.getDataResult(newDoc);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(),null);
        } catch (CRegistrationFailException e) {
            return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "공시단가 문서 수정", description = "공시단가 문서만 수정처리되며 상세 항목은 수정안됨.")
    @RequestMapping(value = "/doc/modify", method = {RequestMethod.POST})
    public CommonResult modifyAnnouncePriceDoc(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePrice data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            announcePriceService.modifyAnnouncePriceDoc(data, Long.valueOf(reqUserUid));
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CViolationCloseException e) {
            return responseService.getFailResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage());
        } catch (CModifictionFailException e) {
            return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
        } catch (CUnknownException e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공시단가 문서 수정 - 업로드처리", description = "공시단가 문서 수정 및 업로드 처리 이전 공시단가 목록은 삭제 후 현재 문서로 다시 업로드")
    @RequestMapping(value = "/doc/modify/upload", method = {RequestMethod.POST})
    public CommonResult modifyAnnouncePriceDocUpload(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePrice data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            announcePriceService.modifyAnnouncePriceDocUpload(data, Long.valueOf(reqUserUid));
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CViolationCloseException e) {
            return responseService.getFailResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage());
        } catch (CModifictionFailException e) {
            return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
        } catch (CUnknownException e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공시단가 문서 삭제", description = "공시단가 문서 삭제처리")
    @RequestMapping(value = "/doc/remove", method = {RequestMethod.POST})
    public CommonResult removeAnnouncePriceDoc(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePrice data)
    {
        try {
            announcePriceService.removeAnnouncePriceDoc(data.getDocNo());
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CViolationCloseException e) {
            return responseService.getFailResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage());
        } catch (CRemoveFailException e) {
            return responseService.getFailResult(CRemoveFailException.getCode(), CRemoveFailException.getCustomMessage());
        } catch (CUnknownException e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    /**
     *  공시단가 문서 확정 처리
     */
    @Operation(summary = "공시단가 문서 확정처리", description = "공시단가 문서 확정처리")
    @RequestMapping(value = "/doc/confirm", method = {RequestMethod.POST})
    public CommonResult cofirmAnnouncePriceDoc(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePrice doc,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            announcePriceService.confirmDoc(doc, Long.valueOf(reqUserUid));
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CNotFoundException e) {
            return responseService.getFailResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage());
        } catch (CAlreadyCloseException e) {
            return responseService.getFailResult(CAlreadyCloseException.getCode(), CAlreadyCloseException.getCustomMessage());
        } catch (CModifictionFailException e) {
            return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
        } catch (CUnknownException e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공시단가 문서 확정 취소처리", description = "공시단가 문서 확정 취소 처리")
    @RequestMapping(value = "/doc/cancel", method = {RequestMethod.POST})
    public CommonResult cancelAnnouncePriceDoc(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePrice doc,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            announcePriceService.cancelDoc(doc, Long.valueOf(reqUserUid));
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CNotFoundException e) {
            return responseService.getFailResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage());
        } catch (CBizProcessFailException e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        } catch (Exception e) {
            log.debug(e.getMessage());
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공시단가 목록 조회", description = "공시단가 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<AnnouncePriceDetail> findAnnouncePriceDetail(
            @Parameter(description = "문서번호") @RequestParam(required = false) String docNo,
            @Parameter(description = "국가코드") @RequestParam(required = false) String countryCd,
            @Parameter(description = "재질코드") @RequestParam(required = false) String materialCd,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "적용일") @RequestParam(required = false) String beginDate,
            @Parameter(description = "원소재구분(B01)") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "강종") @RequestParam(required = false) String steelGrade)
    {
        AnnouncePriceDetail cond = new AnnouncePriceDetail();
        if (StringUtils.isNotBlank(docNo)) cond.setDocNo(docNo);
        if (StringUtils.isNotBlank(countryCd)) cond.setCountryCd(countryCd);
        if (StringUtils.isNotBlank(materialCd)) cond.setMaterialCd(materialCd);
        if (StringUtils.isNotBlank(searchWord)) cond.setMaterialNm(searchWord);
        if (StringUtils.isNotBlank(beginDate)) cond.setBgnDate(beginDate);
        if (StringUtils.isNotBlank(rawMaterialCd)) cond.setRawMaterialCd(rawMaterialCd);
        if (StringUtils.isNotBlank(steelGrade)) cond.setSteelGrade(steelGrade);

        List<AnnouncePriceDetail> resultSet = null;

        try {
            resultSet = announcePriceService.findAnnouncePriceDtl(cond);
            return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "최종 공시단가 목록조회", description = "최종 공시단가 목록 조회")
    @RequestMapping(value = "/last", method = {RequestMethod.GET})
    public ListResult<Map<String, Object>> findLastAnnouncePrice(
            @Parameter(description = "국가코드(C08)") @RequestParam(required = false) String countryCd,
            @Parameter(description = "검색 재질코드 또는 재질명") @RequestParam(required = false) String searchWord,
            @Parameter(description = "재질구분(B01)") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "검색 문서번호 또는 문서명") @RequestParam(required = false) String docNo)
    {
        AnnouncePriceDetail cond = new AnnouncePriceDetail();
        if (StringUtils.isNotBlank(countryCd)) cond.setCountryCd(countryCd);
        if (StringUtils.isNotBlank(rawMaterialCd)) cond.setRawMaterialCd(rawMaterialCd);
        if (StringUtils.isNotBlank(searchWord)) cond.setMaterialNm(searchWord);
        if (StringUtils.isNotBlank(docNo)) cond.setDocNo(docNo);

        List<Map<String, Object>> dataSet = null;
        try {
            dataSet = announcePriceService.findAnnouncePriceLast(cond);
            return responseService.getListResult(dataSet);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공시단가 이력 조회", description = "공시단가 이력 조회")
    @RequestMapping(value = "/history", method = {RequestMethod.GET})
    public ListResult<AnnouncePriceDetail> findAnnouncePriceHistory(
            @Parameter(description = "국가코드(C08)", required = true) @RequestParam String countryCd,
            @Parameter(description = "재질코드", required = true) @RequestParam String materialCd,
            @Parameter(description = "재질명", required = true) @RequestParam String materialNm)
    {
        try {
            List<AnnouncePriceDetail> dataSet = announcePriceService.findAnnouncePriceHistory(countryCd, materialCd, materialNm);
            return responseService.getListResult(dataSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공시단가 상세 정보 조회", description = "공시단가 상세 정보 조회")
    @RequestMapping(value = "/{docNo}/{apSeq}", method = {RequestMethod.GET})
    public DataResult<AnnouncePriceDetail> findAnnouncePriceDetailByKey(
            @Parameter(description = "문서번호", required = true) @PathVariable String docNo,
            @Parameter(description = "항목순번", required = true) @PathVariable Integer apSeq)
    {
        try {
            AnnouncePriceDetail resultSet = announcePriceService.findAnnouncePriceDtlByKey(docNo, apSeq);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CDatabaseException e) {
            return responseService.getDataResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "공시단가 등록", description = "공시단가 등록처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<AnnouncePriceDetail> registrationAnnouncePriceDetail(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePriceDetail data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            AnnouncePriceDetail resultSet = announcePriceService.createAnnouncePriceDtl(data, Long.valueOf(reqUserUid));
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CViolationCloseException e) {
            return responseService.getDataResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage(), null);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult(CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(), null);
        } catch (CRegistrationFailException e) {
            return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
        } catch (CUnknownException e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공시단가 수정", description = "공시단가 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifyAnnouncePriceDetail(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePriceDetail data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        log.info(">>>> modifyAnnouncePriceDetail controller");
        try {
            announcePriceService.modifyAnnouncePriceDtl(data, Long.valueOf(reqUserUid));
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CViolationCloseException e) {
            return responseService.getFailResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage());
        } catch (CModifictionFailException e) {
            return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공시단가 단건 삭제", description = "공시단가 삭제 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removeAnnouncePriceDetail(
            @Parameter(description = "등록정보", required = true) @RequestBody AnnouncePriceDetail data)
    {
        try {
            announcePriceService.removeAnnouncePriceDtl(data);
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CViolationCloseException e) {
            return responseService.getFailResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage());
        } catch (CRemoveFailException e) {
            return responseService.getFailResult(CRemoveFailException.getCode(), CRemoveFailException.getCustomMessage());
        } catch (CUnknownException e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }

        return responseService.getSuccessResult();
    }

    @Operation(summary = "공시단가 고시일 목록 조회", description = "공시단가 고시일 목록 조회")
    @RequestMapping(value = "/doc/announceDate", method = {RequestMethod.GET})
    public ListResult<Map<String, String>> findAnnounceDateList()
    {
        try {
            List<Map<String, String>> dataSet = announcePriceService.findAnnouncedDateList();
            return responseService.getListResult(dataSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "공시단가 기준일 목록 조회", description = "공시단가 기준일 목록 조회")
    @RequestMapping(value = "/doc/applyDate/{docNo}", method = {RequestMethod.GET})
    public ListResult<String> findApplyDateList(
            @Parameter(description = "등록정보", required = true) @PathVariable String docNo)
    {
        try {
            List<String> dataSet = announcePriceService.findApplyDate(docNo);
            return responseService.getListResult(dataSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

}
