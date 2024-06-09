package com.guroomsoft.icms.biz.templateDoc.controller;

import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.service.UserService;
import com.guroomsoft.icms.biz.code.service.CodeService;
import com.guroomsoft.icms.biz.code.service.PartnerService;
import com.guroomsoft.icms.biz.code.service.PlantService;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDoc;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDtl;
import com.guroomsoft.icms.biz.templateDoc.dto.TemplateDtlReq;
import com.guroomsoft.icms.biz.templateDoc.service.TemplateDocService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.AppContant;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "템플릿문서 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/templateDoc")
public class TemplateDocController {
    private final ResponseService responseService;
    private final TemplateDocService templateDocService;
    private final AppConfigService appConfigService;
    private final PlantService plantService;
    private final CodeService codeService;
    private final PartnerService partnerService;
    private final UserService userService;

    @Operation(summary = "템플릿 문서 등록 처리", description = "템플릿 문서 등록 처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<TemplateDoc> registrationTemplateDoc(
            @Parameter(description = "등록정보", required = true) @RequestBody TemplateDoc data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            TemplateDoc dataset = templateDocService.createTemplateDoc(data, reqUserUid);
            if (dataset != null) {
                return responseService.getDataResult(dataset);
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CRegistrationFailException e) {
            return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
        } catch (CDatabaseException e) {
            return responseService.getDataResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage(), null);
        } catch (Exception e) {
            throw new CUnknownException();
        }

        return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
    }

    @Operation(summary = "템플릿 문서 수정 처리", description = "템플릿 문서 수정 처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifyTemplateDoc(
            @Parameter(description = "등록정보", required = true) @RequestBody TemplateDoc data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int updated = templateDocService.modifyTemplateDoc(data, reqUserUid);
            if (updated > 0) {
                return responseService.getSuccessResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CViolationCloseException e) {
            return responseService.getFailResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage());
        } catch (CModifictionFailException e) {
            return responseService.getFailResult(CModifictionFailException.getCode(), CModifictionFailException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }

        return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
    }

    @Operation(summary = "세부항목 등록 처리", description = "세부항목 등록 처리")
    @RequestMapping(value = "/details/registration", method = {RequestMethod.POST})
    public DataResult<TemplateDtl> registrationTemplateDtl(
            @Parameter(description = "등록정보", required = true) @RequestBody TemplateDtl data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            TemplateDtl dataset = templateDocService.createTemplateDtl(data, Long.valueOf(reqUserUid));
            if (dataset != null) {
                return responseService.getDataResult(dataset);
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CRegistrationFailException e) {
            return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
        } catch (CDatabaseException e) {
            return responseService.getDataResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage(), null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }

        return responseService.getDataResult(CRegistrationFailException.getCode(), CRegistrationFailException.getCustomMessage(), null);
    }


    @Operation(summary = "업로드 유효성 체크", description = "유효성 체크")
    @RequestMapping(value = "/checkValidation", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> checkValiddation(
            @Parameter(description = "업로드 목록", required = true) @RequestBody List<TemplateDtl> items)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        resultMap = templateDocService.checkValidItems(items);
        return responseService.getDataResult(resultMap);
    }

    @Operation(summary = "템플릿 문서 삭제 처리", description = "템플릿 문서 삭제 처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removeTemplateDoc(
            @Parameter(description = "템플릿 문서 코드", required = true) @RequestParam String docNo)
    {
        try {
            int updated = templateDocService.removeTemplateDoc(docNo);
            if (updated > 0) {
                return responseService.getSuccessResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CNotFoundException e) {
            return responseService.getFailResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage());
        } catch (CViolationCloseException e) {
            return responseService.getFailResult(CViolationCloseException.getCode(), CViolationCloseException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }

        return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
    }

    @Operation(summary = "템플릿 문서 확정 처리", description = "템플릿 문서 확정 처리")
    @RequestMapping(value = "/confirm", method = {RequestMethod.POST})
    public CommonResult confirmTemplateDoc(
            @Parameter(description = "문서번호", required = true) @RequestParam String docNo,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            TemplateDoc doc = new TemplateDoc();
            doc.setDocNo(docNo);
            doc.setModUid(Long.valueOf(reqUserUid));
            int updated = templateDocService.confirmDoc(doc, Long.valueOf(reqUserUid));
            if (updated > 0) {
                return responseService.getSuccessResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }

        return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
    }

    @Operation(summary = "확정 문서 취소 처리", description = "확정 문서 취소 처리")
    @RequestMapping(value = "/cancel", method = {RequestMethod.POST})
    public CommonResult cancelTemplateDoc(
            @Parameter(description = "문서번호", required = true) @RequestParam String docNo,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            TemplateDoc doc = new TemplateDoc();
            doc.setDocNo(docNo);
            doc.setModUid(Long.valueOf(reqUserUid));
            int updated = templateDocService.cancelDoc(doc, Long.valueOf(reqUserUid));
            if (updated > 0) {
                return responseService.getSuccessResult();
            }
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }

        return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
    }

    @Operation(summary = "템플릿 문서 목록 조회", description = "템플릿 문서 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public ListResult<TemplateDoc> findTemplateDoc(
            @Parameter(description = "작성자UID") @RequestParam(required = false) Long writerUid,
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "협력사코드") @RequestParam(required = false) String bpCd,
            @Parameter(description = "검색할 문서번호 또는 문서명") @RequestParam(required = false) String searchDoc,
            @Parameter(description = "문서상태") @RequestParam(required = false) String docStatus,
            @Parameter(description = "차종") @RequestParam(required = false) String carModel,
            @Parameter(description = "매입품번") @RequestParam(required = false) String searchItem)
    {
        Map<String, Object> cond = new HashMap<>();

        if (StringUtils.isNotBlank(searchDoc)) cond.put("searchDoc", searchDoc);
        if (StringUtils.isNotBlank(plantCd)) cond.put("plantCd", plantCd);
        if (StringUtils.isNotBlank(docStatus)) cond.put("docStatus", docStatus);
        if (StringUtils.isNotBlank(bpCd)) cond.put("bpCd", bpCd);
        if (writerUid.longValue() > 0) cond.put("writerUid", writerUid);
        if (StringUtils.isNotBlank(carModel)) cond.put("carModel", carModel);
        if (StringUtils.isNotBlank(searchItem)) cond.put("searchItem", searchItem);

        List<TemplateDoc> dataset = null;
        try {
            dataset = templateDocService.findTemplateDoc(cond);
            return responseService.getListResult(dataset);
        } catch (CNotFoundException e) {
            return responseService.getListResult(null);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "문서목록 엑셀 내보내기", description = "문서목록 엑셀 내보내기")
    @RequestMapping(value = "/exportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcel(
            @Parameter(description = "작성자UID") @RequestParam(required = false) Long writerUid,
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "협력사코드") @RequestParam(required = false) String bpCd,
            @Parameter(description = "검색할 문서번호 또는 문서명") @RequestParam(required = false) String searchDoc,
            @Parameter(description = "문서상태") @RequestParam(required = false) String docStatus,
            @Parameter(description = "차종") @RequestParam(required = false) String carModel,
            @Parameter(description = "매입품번") @RequestParam(required = false) String searchItem)
    {
        Map<String, Object> cond = new HashMap<>();

        if (StringUtils.isNotBlank(searchDoc)) cond.put("searchDoc", searchDoc);
        if (StringUtils.isNotBlank(plantCd)) cond.put("plantCd", plantCd);
        if (StringUtils.isNotBlank(docStatus)) cond.put("docStatus", docStatus);
        if (StringUtils.isNotBlank(bpCd)) cond.put("bpCd", bpCd);
        if (writerUid.longValue() > 0) cond.put("writerUid", writerUid);
        if (StringUtils.isNotBlank(carModel)) cond.put("carModel", carModel);
        if (StringUtils.isNotBlank(searchItem)) cond.put("searchItem", searchItem);

        String reportTitle = "협력사품목 문서목록";
        String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

        try {
            List<TemplateDoc> resultList = templateDocService.findTemplateDoc(cond);


            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            condMap.put("cond0", String.format("[검색어] : %s", StringUtils.defaultString(searchDoc, "전체")));
            if (StringUtils.isNotBlank(plantCd)) {
                condMap.put("cond1", "[플랜트] : 전체");
            } else {
                condMap.put("cond1", String.format("[플랜트] : %s - %s", plantCd, plantService.getName(plantCd)));
            }

            if (StringUtils.isNotBlank(docStatus)) {
                condMap.put("cond2", "[문서상태] : 전체");
            } else {
                condMap.put("cond2", String.format("[문서상태] : %s", codeService.getCodeNameByKey(AppContant.CodeGroupValue.DOC_STATUS.getValue(), docStatus)));
            }

            if (StringUtils.isNotBlank(bpCd)) {
                condMap.put("cond3", "[협력사] : 전체");
            } else {
                condMap.put("cond3", String.format("[협력사] : %s - %s", bpCd, partnerService.getName(bpCd)));
            }
            User user = userService.findUserByKey(Long.valueOf(writerUid));
            if (user != null && StringUtils.isNotBlank(user.getAccountName())) {
                condMap.put("cond4", String.format("[작성자] : %s", user.getAccountName()));
            } else {
                condMap.put("cond4", "전체");
            }
            condMap.put("cond5", String.format("[차종] : %s", StringUtils.defaultString(carModel, "전체")));
            condMap.put("cond6", String.format("[매입품번] : %s", StringUtils.defaultString(searchItem, "전체")));

            String[] colHeaders = {"No", "문서번호", "문서명", "파일명", "작성일시", "작성자", "문서상태", "확정일시",
                    "비고", "등록일시", "등록자", "수정일시", "수정자"};

            return templateDocService.exportToExcel(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultList);
        } catch (CNotFoundException e) {
            return templateDocService.exportToExcel(
                    excelFileName,
                    reportTitle,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Operation(summary = "템플릿 문서 상세 조회", description = "템플릿 문서 상세 조회")
    @RequestMapping(value = "/{docNo}", method = {RequestMethod.GET})
    public DataResult<TemplateDoc> findTemplateDocByKey(
            @Parameter(description = "문서번호", required = true) @PathVariable String docNo)
    {
        try {
            TemplateDoc resultSet = templateDocService.findTemplateDocByKey(docNo);
            return responseService.getDataResult(resultSet);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (CNotFoundException e) {
            return responseService.getDataResult(CNotFoundException.getCode(), CNotFoundException.getCustomMessage(), null);
        } catch (CDatabaseException e) {
            return responseService.getDataResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "템플릿 문서 상세 품목 리스트 조회", description = "템플릿 문서 상세 품목 리스트 조회")
    @RequestMapping(value = "/details/{docNo}", method = {RequestMethod.GET})
    public ListResult<TemplateDtl> findTemplateDtlWithDocNo(
            @Parameter(description = "문서번호", required = true) @PathVariable String docNo,
            @Parameter(description = "페이지번호", required = true) @RequestParam(defaultValue = "1") int pageNumber,
            @Parameter(description = "페이지당 행수", required = true) @RequestParam(defaultValue = "20") int rowCountPerPage)
    {
        TemplateDtlReq cond = new TemplateDtlReq();
        cond.setDocNo(docNo);
        List<TemplateDtl> resultSet = null;

        try {
            // 디폴트 페이지 당 조회 건수
            if (rowCountPerPage == 0) {
                rowCountPerPage = appConfigService.getIntValue(AppConfigService.ROWCOUNT_PER_PAGE);
            }

            cond.setRowCountPerPage(rowCountPerPage);
            cond.setTotalCount( templateDocService.getCountDtlWithinDoc(cond) );
            int rc = cond.getRowCountPerPage();
            int share = cond.getTotalCount() / rc;
            int remainder = cond.getTotalCount() % rc;

            if (remainder > 0) share++;
            cond.setTotalPageCount(share);
            cond.setPageNumber(pageNumber);

            // 조회 요청 페이지가 전체 페이지수 보다 크면 빈 목록 반환
            if (cond.getTotalPageCount() < cond.getPageNumber()) {
                return responseService.getListPageResult(null, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
            }

            cond.setStartOffset((cond.getPageNumber()-1) * rowCountPerPage);
            resultSet = templateDocService.findTemplateDtlWithinDoc(cond);
            return responseService.getListPageResult(resultSet, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }


    @Operation(summary = "협력업체 품목 조회", description = "협력업체 품목 조회")
    @RequestMapping(value = "/bpitem", method = {RequestMethod.GET})
    public ListResult<TemplateDtl> findBpItem(
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "협력사코드") @RequestParam(required = false) String bpCd,
            @Parameter(description = "차종") @RequestParam(required = false) String carModel,
            @Parameter(description = "부품구분") @RequestParam(required = false) String partType,
            @Parameter(description = "매입품번") @RequestParam(required = false) String searchItem,
            @Parameter(description = "SUB부품협력사코드") @RequestParam(required = false) String pcsSubItemBp,
            @Parameter(description = "검색SUB품번") @RequestParam(required = false) String searchSubItem,
            @Parameter(description = "원소재코드") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "검색재질코드") @RequestParam(required = false) String searchMaterial,
            @Parameter(description = "페이지번호", required = true) @RequestParam(defaultValue = "1") int pageNumber,
            @Parameter(description = "페이지당 행수", required = true) @RequestParam(defaultValue = "20") int rowCountPerPage)
    {
        TemplateDtlReq cond = new TemplateDtlReq();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(bpCd)) cond.setBpCd(bpCd);
        if (StringUtils.isNotBlank(carModel)) cond.setCarModel(carModel);
        if (StringUtils.isNotBlank(partType)) cond.setPartType(partType);
        if (StringUtils.isNotBlank(searchItem)) cond.setPcsItemNo(searchItem);
        if (StringUtils.isNotBlank(pcsSubItemBp)) cond.setPcsSubItemBp(pcsSubItemBp);
        if (StringUtils.isNotBlank(searchSubItem)) cond.setSubItemNo(searchSubItem);
        if (StringUtils.isNotBlank(rawMaterialCd)) cond.setRawMaterialCd(rawMaterialCd);
        if (StringUtils.isNotBlank(searchMaterial)) cond.setMaterialCd(searchMaterial);

        List<TemplateDtl> resultSet = null;

        try {
            // 디폴트 페이지 당 조회 건수
            if (rowCountPerPage == 0) {
                rowCountPerPage = appConfigService.getIntValue(AppConfigService.ROWCOUNT_PER_PAGE);
            }

            cond.setRowCountPerPage(rowCountPerPage);
            cond.setTotalCount( templateDocService.getBpItemCount(cond) );
            int rc = cond.getRowCountPerPage();
            int share = cond.getTotalCount() / rc;
            int remainder = cond.getTotalCount() % rc;

            if (remainder > 0) share++;
            cond.setTotalPageCount(share);
            cond.setPageNumber(pageNumber);

            // 조회 요청 페이지가 전체 페이지수 보다 크면 빈 목록 반환
            if (cond.getTotalPageCount() < cond.getPageNumber()) {
                return responseService.getListPageResult(null, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
            }

            cond.setStartOffset((cond.getPageNumber()-1) * rowCountPerPage);
            resultSet = templateDocService.findBpItem(cond);
            return responseService.getListPageResult(resultSet, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "세부항목 엑셀 내보내기", description = "세부항목 엑셀 내보내기")
    @RequestMapping(value = "/bpItem/exportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportItemToExcel(
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "협력사코드") @RequestParam(required = false) String bpCd,
            @Parameter(description = "차종") @RequestParam(required = false) String carModel,
            @Parameter(description = "부품구분") @RequestParam(required = false) String partType,
            @Parameter(description = "매입품번") @RequestParam(required = false) String searchItem,
            @Parameter(description = "SUB부품협력사코드") @RequestParam(required = false) String pcsSubItemBp,
            @Parameter(description = "검색SUB품번") @RequestParam(required = false) String searchSubItem,
            @Parameter(description = "원소재코드") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "검색재질코드") @RequestParam(required = false) String searchMaterial)
    {
        TemplateDtlReq cond = new TemplateDtlReq();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(bpCd)) cond.setBpCd(bpCd);
        if (StringUtils.isNotBlank(carModel)) cond.setCarModel(carModel);
        if (StringUtils.isNotBlank(partType)) cond.setPartType(partType);
        if (StringUtils.isNotBlank(searchItem)) cond.setPcsItemNo(searchItem);
        if (StringUtils.isNotBlank(pcsSubItemBp)) cond.setPcsSubItemBp(pcsSubItemBp);
        if (StringUtils.isNotBlank(searchSubItem)) cond.setSubItemNo(searchSubItem);
        if (StringUtils.isNotBlank(rawMaterialCd)) cond.setRawMaterialCd(rawMaterialCd);
        if (StringUtils.isNotBlank(searchMaterial)) cond.setMaterialCd(searchMaterial);

        List<TemplateDtl> resultSet = null;

        String reportTitle = "협력사품목";
        String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

        try {
            List<TemplateDtl> resultList = templateDocService.findBpItem(cond);

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            if (StringUtils.isNotBlank(plantCd)) {
                condMap.put("cond0", "[플랜트] : 전체");
            } else {
                condMap.put("cond0", String.format("[플랜트] : %s - %s", plantCd, plantService.getName(plantCd)));
            }

            if (StringUtils.isNotBlank(bpCd)) {
                condMap.put("cond1", "[협력사] : 전체");
            } else {
                condMap.put("cond1", String.format("[협력사] : %s - %s", bpCd, partnerService.getName(bpCd)));
            }

            if (StringUtils.isNotBlank(bpCd)) {
                condMap.put("cond2", "[부품구분] : 전체");
            } else {
                condMap.put("cond2", String.format("[부품구분] : %s", codeService.getCodeNameByKey(AppContant.CodeGroupValue.PART_TYPE.getValue(), partType)));
            }

            if (StringUtils.isNotBlank(bpCd)) {
                condMap.put("cond3", "[원소재] : 전체");
            } else {
                condMap.put("cond3", String.format("[원소재] : %s", codeService.getCodeNameByKey(AppContant.CodeGroupValue.MATERIAL_TYPE.getValue(), rawMaterialCd)));
            }

            if (StringUtils.isNotBlank(carModel)) {
                condMap.put("cond4", "[차종] : 전체");
            } else {
                condMap.put("cond4", String.format("[차종] : %s", carModel));
            }

            if (StringUtils.isNotBlank(searchItem)) {
                condMap.put("cond5", "[매입품번] : 전체");
            } else {
                condMap.put("cond5", String.format("[매입품번] : %s", searchItem));
            }

            if (StringUtils.isNotBlank(searchSubItem)) {
                condMap.put("cond6", "[SUB 품번] : 전체");
            } else {
                condMap.put("cond6", String.format("[SUB 품번] : %s", searchSubItem));
            }

            if (StringUtils.isNotBlank(searchMaterial)) {
                condMap.put("cond7", "[재질코드] : 전체");
            } else {
                condMap.put("cond7", String.format("[재질코드] : %s", searchMaterial));
            }


            String[] colHeaders = {"No", "플래트코드", "플랜트", "협력사코드", "거래처명", "차종", "매입품번", "부품구분코드",
                    "부품구분명", "사급품 협력사코드", "사급품 거래처명", "SUB 품번", "SUB 품명", "원소재코드", "원소재명", "재질코드", "재질명",
                    "US", "강종", "M-Spec", "M-Type", "두께/두께", "가로/외경", "세로/투입길이", "BL-가로", "BL-세로",
                    "BL-CAVITY", "NET중량(Kg)", "비중", "SLITT LOSS 율(%)", "TO LOSS 율(%)", "투입중량(Kg)", "Scrap 중량(Kg/EA)",
                    "Scrap 회수율(%)", "재관비율(%)", "외주재관비율(%)", "작성자 ID", "문서번호"};

            return templateDocService.exportItemToExcel(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultList);
        } catch (CNotFoundException e) {
            return templateDocService.exportItemToExcel(
                    excelFileName,
                    reportTitle,
                    null,
                    null,
                    null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }
}
