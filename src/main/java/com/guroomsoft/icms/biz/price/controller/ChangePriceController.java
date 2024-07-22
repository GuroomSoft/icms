package com.guroomsoft.icms.biz.price.controller;

import com.guroomsoft.icms.biz.agreement.dto.AgreementDoc;
import com.guroomsoft.icms.biz.agreement.service.AgreementService;
import com.guroomsoft.icms.biz.code.service.PlantService;
import com.guroomsoft.icms.biz.econtract.service.EformService;
import com.guroomsoft.icms.biz.price.dto.ChangePrice;
import com.guroomsoft.icms.biz.price.dto.DetailReq;
import com.guroomsoft.icms.biz.price.dto.PriceChange;
import com.guroomsoft.icms.biz.price.dto.PurchaseItemReq;
import com.guroomsoft.icms.biz.price.service.ChangePriceService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.DateTimeUtil;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Tag(name = "가격변경관리 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/price/change")
public class ChangePriceController {
    private final ResponseService responseService;
    private final ChangePriceService changePriceService;
    private final PlantService plantService;
    private final EformService eformService;
    private final AgreementService agreementService;

    @Operation(summary = "가격변경 상세 목록 조회", description = "가격변경 상세 목록 조회")
    @RequestMapping(value = "/detail", method = {RequestMethod.POST})
    public ListResult<ChangePrice> findChangePriceList(
            @Parameter(description = "조회조건", required = true) @RequestBody DetailReq cond)
    {
        List<ChangePrice> resultSet = null;
        try {
            if (cond.getBpList() != null && cond.getBpList().isEmpty()) cond.setBpList(null);
            resultSet = changePriceService.findChangedPrice(cond);

            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "가격변경 상세 목록 조회 엑셀 출력", description = "가격변경 상세 목록 조회 엑셀 출력")
    @RequestMapping(value = "/detailToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> findChangePriceListToExcel(
            //@Parameter(description = "조회조건", required = true) @RequestParam DetailReq cond
            @Parameter(description = "공시단가문서번호") @RequestParam(required = false) String docNo,
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd,
            @Parameter(description = "협력사코드목록") @RequestParam(required = false) List<String> bpList,
            @Parameter(description = "차종") @RequestParam(required = false) String carModel,
            @Parameter(description = "부품구분코드") @RequestParam(required = false) String partType,
            @Parameter(description = "원소재코드") @RequestParam(required = false) String rawMaterialCd,
            @Parameter(description = "매입품번") @RequestParam(required = false) String pcsItemNo,
            @Parameter(description = "SUB품번") @RequestParam(required = false) String subItemNo,
            @Parameter(description = "재질코드") @RequestParam(required = false) String materialCd
    )
    {
        try {
            List<ChangePrice> resultSet = null;
            DetailReq cond = new DetailReq();
            cond.setDocNo(docNo);
            cond.setPlantCd(plantCd);
            cond.setBpList(bpList);
            cond.setCarModel(carModel);
            cond.setPartType(partType);
            cond.setRawMaterialCd(rawMaterialCd);
            cond.setPcsItemNo(pcsItemNo);
            cond.setSubItemNo(subItemNo);
            cond.setMaterialCd(materialCd);

            resultSet = changePriceService.findChangedPrice(cond);

            String reportTitle = "가격변경 상세 목록 조회";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            if (StringUtils.isNotBlank(docNo)) {
                condMap.put("cond0", "[문서] : 전체");
            } else {
                condMap.put("cond0", String.format("[문서] : %s - %s", docNo, docNo));
            }
            if (StringUtils.isNotBlank(plantCd)) {
                condMap.put("cond1", "[플랜트] : 전체");
            } else {
                condMap.put("cond1", String.format("[플랜트] : %s - %s", plantCd, plantService.getName(plantCd)));
            }

            String[] colHeaders = {"NO", "기준일", "플랜트코드", "플랜트명", "협력사코드", "협력사명", "차종"
                    , "매입품번", "매입품명", "현재가입자", "현재가", "변경금액합계", "새 적용금액", "부품구분코드", "부품구분명", "사급품목 협력사 코드", "사급품목 협력사 명", "SUB 품번", "SUB 품명"
                    , "원소재코드", "원소재명", "기준일", "재질코드", "재질명", "US", "강종", "M-SPEC", "M-TYPE", "두께/두께", "가로/외경", "세로/투입길이", "BL-가로"
                    , "BL-세로", "BL-CAVITY", "NET중량(Kg)", "비중", "SLITTLOSS 율(%)", "LOSS 율(%)", "투입중량(Kg)", "사급단가(이전)", "사급단가(이후)", "사급단가 차액", "사급재료비(이전/매)"
                    , "사급재료비(이후/매)", "사급재료비 차액", "SCRAP 단가(이전/Kg)", "SCRAP 단가(이후/Kg)", "SCRAP 단가 차액", "SCRAP 단가 중량(Kg/EA)", "SCRAP 단가 회수율(%)", "SCRAP 가격(이전/매)"
                    , "SCRAP 가격(이후/매)", "SCRAP 가격 차액", "부품재료비(이전)", "부품재료비(이후)", "부품재료비 차액", "재관비율(%)", "외주재관비율(%)", "변동금액", "변경상태", "SUB품목수", "가격변경완료수"
                    , "미완료수", "에러수"
            };

            return changePriceService.exportToExcelForDetail(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultSet);
            //return responseService.getListResult(resultSet);
        } catch (CNotFoundException e) {
            throw new CNotFoundException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "협력사 목록 조회", description = "협력사 목록 조회")
    @RequestMapping(value = "/targetBp", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> findTargetBp(
            @Parameter(description = "공시단가문서", required = true) @RequestParam String docNo,
            @Parameter(description = "고시일(YYYYMMDD)", required = true) @RequestParam String announcedDate,
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd)
    {
        List<Map<String, Object>> resultSet = null;
        try {
            resultSet = changePriceService.findTargetBpList(announcedDate, plantCd, docNo);
            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "매입품목 기준 가격변경 조회", description = "매입품목 기준 가격변경 목록 조회")
    @RequestMapping(value = "/purchaseItem", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> findPurchaseItemList(
            @Parameter(description = "조회조건", required = true) @RequestBody PurchaseItemReq search)
    {
        if (search.getBpList() != null && search.getBpList().isEmpty()) search.setBpList(null);

        try {
            List<Map<String, Object>> resultSet = changePriceService.findChangePricePurchaseItem(search);
            return responseService.getListResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "가격결정합의 내용 조회", description = "가격결정합의 내용 조회")
    @RequestMapping(value = "/agreement/content", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> getArgeementContent(
            @Parameter(description = "등록월", required = true) @RequestParam String announcedDate,
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(description = "협력사 코드", required = true) @RequestParam String bpCd,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        PurchaseItemReq cond = new PurchaseItemReq();
        cond.setAnnouncedDate(announcedDate);
        cond.setPlantCd(plantCd);
        if (StringUtils.isNotBlank(bpCd)) cond.setBpCd(bpCd);

        try {
            Map<String, Object> resultSet = changePriceService.getAgreementContent(cond, Long.valueOf(reqUserUid));
            return responseService.getDataResult(resultSet);
        } catch (CDatabaseException e) {
            throw new CDatabaseException();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "플랜트별 가격 변경 계산 처리 후 가격결정합의서 생성", description = "플랜트별 가격 변경 계산 처리 후 가격결정합의서 생성")
    @RequestMapping(value = "/calculate", method = {RequestMethod.POST})
    public CommonResult calculateChangePrice(
            @Parameter(description = "플랜트 코드", required = true) @RequestParam String plantCd,
            @Parameter(description = "등록월", required = true) @RequestParam String announcedDate,
            @Parameter(description = "공시단가문서", required = true) @RequestParam String docNo,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            //changePriceService.calculateChangePrice(plantCd, announcedDate.replaceAll("[^0-9]", ""), Long.valueOf(reqUserUid));
            changePriceService.calculateChangePrice(plantCd, announcedDate, Long.valueOf(reqUserUid), docNo);
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CBizProcessFailException e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "가격변경 확정 처리", description = "사용자가 지정한 문서번호 목록을 기준으로 가격변경 확정 처리")
    @RequestMapping(value = "/confirm", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> confirmPriceChange(
            @Parameter(description = "확정 문서번호 목록", required = true) @RequestBody List<String> docs,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (docs == null || docs.isEmpty())
            {
                resultMap.put("totalCount", Integer.valueOf(0));
                resultMap.put("successCount", Integer.valueOf(0));
                resultMap.put("failCount", Integer.valueOf(0));
            }

            int successCount = changePriceService.confirmPriceChangeDoc(docs, Long.valueOf(reqUserUid));

            resultMap.put("totalCount", docs.size());
            resultMap.put("successCount", Integer.valueOf(successCount));
            resultMap.put("failCount", docs.size() - successCount);
            return responseService.getDataResult(resultMap);
        } catch (CInvalidArgumentException e) {
            return responseService.getDataResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage(), null);
        } catch (SQLServerException e) {
            return responseService.getDataResult(CDatabaseException.getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "가격변경 확정 취소 처리", description = "사용자가 지정한 문서번호 목록을 기준으로 가격변경 확정 취소 처리")
    @RequestMapping(value = "/cancelConfirm", method = {RequestMethod.DELETE})
    public CommonResult cancelConfirmPriceChange(
            @Parameter(description = "확정 문서번호 목록", required = true) @RequestBody List<String> docs,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            //1. BT_PRICE_CHANGE 테이블에서 검색
            //1. BT_PRICE_CHANGE 테이블 doc_status = 'N' 으로 수정 및 데이터 가져오기 해당
            List<PriceChange> docList = changePriceService.findBtPriceChangeList(docs);
            //2. BT_PRICE_CHANGE 테이블  doc_status = 'N' 으로 수정

            List<String> srcDocList = docList.stream().map(PriceChange::getDocNo).toList();
            List<AgreementDoc> agreementList = agreementService.findAgreementForDocNo(srcDocList);
            //3. HT_AGREEMENT 테이블 데이터 검색 SELECT * FROM HT_AGREEMENT WHERE src_doc_no IN docList.doc_no

            if(agreementList != null && !agreementList.isEmpty())
            {
                List<String> eFormDocIds = agreementList.stream()
                        .map(AgreementDoc::getEformDocId)
                        .filter(Objects::nonNull) // null 방지
                        .toList();
                if(!eFormDocIds.isEmpty())
                {
                    // eformsign 문서삭제 => 문서 취소
                     eformService.delDocuments(null, eFormDocIds);
                    // eformService.cancelDocuments(null, eFormDocIds);

                    agreementService.deleteAgreementLog(eFormDocIds);
                    //4. HT_AGREEMENT_STATUS_LOG 테이블 HT_AGREEMENT_STATUS_LOG.doc_id  = HT_AGREEMENT.eform_doc_id 삭제
                }
            }
            agreementService.deleteAgreement(srcDocList);
            //5. HT_AGREEMENT 테이블 데이터 삭제

            changePriceService.disabledPriceChange(docs);
            //2. BT_PRICE_CHANGE 테이블  doc_status = 'N' 으로 수정


            // 참고 프로시저 SP_CREATE_ECONTRACT_AGREEMENT, SP_CANCEL_PRICE_CHANGE
            //{
            //    call SP_CANCEL_PRICE_CHANGE(
            //    #{pDocNo,           mode=IN, jdbcType=NVARCHAR, javaType=String}
            //  , #{pReqUserUid,      mode=IN, jdbcType=NUMERIC, javaType=Long}
            //  , #{pEfid,            mode=OUT, jdbcType=VARCHAR, javaType=String}
            //  , #{pErrCode,         mode=OUT, jdbcType=NUMERIC, javaType=Integer}
            //  , #{pErrMsg,          mode=OUT, jdbcType=VARCHAR, javaType=String})
            //}
            //if (((Integer)param.get("pErrCode")).intValue() < 0 ) {
            //    throw new CBizProcessFailException();
            //}
            return responseService.getSuccessResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CBizProcessFailException e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        } catch (CDatabaseException e) {
            return responseService.getFailResult(CDatabaseException.getCode(), CDatabaseException.getCustomMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "SAP 매입 단가 엑셀 내보내기", description = "SAP 매입 단가 엑셀 내보내기")
    @RequestMapping(value = "/SapExportToExcelPurchase", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelForPurchaseSap(
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd)
    {
        ChangePrice cond = new ChangePrice();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);

        try {
            List<ChangePrice> resultSet = changePriceService.findPurchaseForSap(cond);
            String reportTitle = "매입단가 업로드 작성";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            //if (StringUtils.isNotBlank(plantCd)) {
            //    condMap.put("cond0", "[플랜트] : 전체");
            //} else {
            //    condMap.put("cond0", String.format("[플랜트] : %s - %s", plantCd, plantService.getName(plantCd)));
            //}

            String[] colHeaders = {"소급반영일", "구매그룹", "품의번호", "공급업체코드", "공급업체명", "자재코드", "단위"
                    , "통화", "신규단가", "단가유형", "적용일자", "정산단가", "기간(FROM)1", "기간(TO)1", "정산사유1", "정산단가1"
            };

            return changePriceService.exportToExcelForPurchaseSap(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultSet);
        } catch (CNotFoundException e) {
            throw new CNotFoundException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

    @Operation(summary = "SAP 사급 단가 엑셀 내보내기", description = "SAP 사급 단가 엑셀 내보내기")
    @RequestMapping(value = "/SapExportToExcel", method = {RequestMethod.POST})
    public ResponseEntity<byte[]> exportToExcelForSap(
            @Parameter(description = "플랜트코드") @RequestParam(required = false) String plantCd)
    {
        ChangePrice cond = new ChangePrice();
        if (StringUtils.isNotBlank(plantCd)) cond.setPlantCd(plantCd);

        try {
            List<ChangePrice> resultSet = changePriceService.findConsignedForSap(cond);
            String reportTitle = "사급단가 업로드 작성";
            String excelFileName = String.format("%s_%s.xlsx", reportTitle, DateTimeUtil.currentDatetime2());

            // 조회조건 영역
            Map<String, Object> condMap = new HashMap<String, Object>();
            //if (StringUtils.isNotBlank(plantCd)) {
            //    condMap.put("cond0", "[플랜트] : 전체");
            //} else {
            //    condMap.put("cond0", String.format("[플랜트] : %s - %s", plantCd, plantService.getName(plantCd)));
            //}

            String[] colHeaders = {"필드명", "영업조직", "영업조직명", "유통채널", "유통채널명", "판매처", "차종"
                    , "차종명", "자재", "자재명", "통화", "종전판가", "신규판가", "판가차이", "기준수량", "판가유형", "적용관세"
                    , "적용일자", "PLANT", "소급유형(1)", "시작일(1)", "종료일(1)", "소급단가(1)"
            };

            return changePriceService.exportToExcelForSap(
                    excelFileName,
                    reportTitle,
                    condMap,
                    colHeaders,
                    resultSet);
        } catch (CNotFoundException e) {
            throw new CNotFoundException();
        } catch (Exception e) {
            throw new CUnknownException();
        }
    }

}
