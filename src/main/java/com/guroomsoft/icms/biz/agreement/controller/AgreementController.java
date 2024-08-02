package com.guroomsoft.icms.biz.agreement.controller;

import com.guroomsoft.icms.biz.agreement.dto.AgreementContentReq;
import com.guroomsoft.icms.biz.agreement.service.AgreementService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "합의서(전자계약) Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agreement")
public class AgreementController {
    private final ResponseService responseService;
    private final AgreementService agreementService;

    @Operation(summary = "합의서 전송데이터 조회", description = "합의서 전송데이터 조회")
    @RequestMapping(value = "/getTargetData", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> getTargetData(
            @Parameter(description = "조회조건", required = true) @RequestBody AgreementContentReq cond,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        List<Map<String, Object>> resultSet  = null;
        try {
            resultSet = agreementService.findAgreementForSend(cond.getPlantCd(), cond.getAnnouncedDate(), cond.getBpList(), cond.getDocNo(), Long.valueOf(reqUserUid));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "합의서 데이터 조회(이폼사인 문서번호로 조회)", description = "합의서 데이터 조회(이폼사인 문서번호로 조회)")
    @RequestMapping(value = "/getEformDocData", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> getTargetData(
            @Parameter(description = "조회조건", required = true) @RequestBody List<String> cond)
    {
        List<Map<String, Object>> resultSet  = null;

        try {
            resultSet = agreementService.findAgreementForEformDocId(cond);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "전자계약용 합의서 데이터 생성", description = "전자계약용 합의서 데이터 생성")
    @RequestMapping(value = "/createAgreement", method = {RequestMethod.POST})
    public CommonResult createAgreement(
            @Parameter(description = "조회조건", required = true) @RequestBody AgreementContentReq cond)
    {
        List<String> b  = null;
        try {
            int totalCount = agreementService.createTargetAgreementDoc(cond.getAnnouncedDate(), cond.getPlantCd(), cond.getBpList());
            if (totalCount > 0) {
                return responseService.getSuccessResult();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return responseService.getFailResult();
    }

    @Operation(summary = "페이지 헤더 알림 정보", description = "최근 가격변경 합의서 상태변경 목록 조회, 내부사용자는 accountId")
    @RequestMapping(value = "/getHeaderForAgreement", method = {RequestMethod.GET})
    public ListResult<Map<String, Object>> getHeaderNotification(@Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            List<Map<String, Object>> result = agreementService.getHeaderNotification(Long.valueOf(reqUserUid));
            return responseService.getListResult(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*

    @Operation(summary = "전자계약용 합의서 상세 업데이트", description = "전자계약용 합의서 상세 업데이트")
    @RequestMapping(value = "/modify/detail", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> modifyAgreementDetail(
            @Parameter(description = "업데이트 조건", required = true) @RequestBody AgreementContentReq cond)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        if (StringUtils.isBlank(cond.getPlantCd()) || StringUtils.isBlank(cond.getAnnouncedDate())
                || cond.getBpList() == null || cond.getBpList().isEmpty())
        {
            return responseService.getDataResult(null);
        }
        int totalBps = cond.getBpList().size();
        try {
            int totalCount = agreementService.updateDetail(cond.getPlantCd(), cond.getAnnouncedDate(), cond.getBpList());
            resultMap.put("successCount", Integer.valueOf(totalCount));
            resultMap.put("failCount", Integer.valueOf(totalBps - totalCount));
            responseService.getDataResult(resultMap);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return responseService.getDataResult(null);
    }

    */
}
