package com.guroomsoft.icms.biz.econtract.controller;

import com.guroomsoft.icms.biz.econtract.dao.EformMemberDAO;
import com.guroomsoft.icms.biz.econtract.dto.EformMember;
import com.guroomsoft.icms.biz.econtract.service.EformService;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Tag(name = "전자계약 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/econtract")
public class EContractController {
    private final ResponseService responseService;
    private final EformService eformService;
    private final EformMemberDAO eformMemberDAO;

    @Operation(summary = "eformsign 인증토큰", description = "eformsign 인증토큰")
    @RequestMapping(value = "/eformsign/authToken", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getAuthTokenEformSign(
            @Parameter(description = "이폼사인 멤버ID(미지정 시 디폴트 계정 사용)") @RequestParam(required = false) String memberId)
    {
        try {
            Map<String, Object> resultMap = eformService.getAccessToken(memberId);
            return responseService.getDataResult(resultMap);
        } catch (Exception e) {
            log.error("👉 {}", e.getMessage());
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "eformsign 템플릿 목록 ", description = "eformsign 템플릿 목록 ")
    @RequestMapping(value = "/eformsign/form", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getEformFormList()
    {
        try {
            Map<String, Object> resultMap = eformService.getForm(null);
            return responseService.getDataResult(resultMap);
        } catch (Exception e) {
            log.error("👉 {}", e.getMessage());
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "eformsign 문서함 문서 목록 ", description = "eformsign 문서함 문서 목록 ")
    @RequestMapping(value = "/eformsign/documents/{type}", method = {RequestMethod.POST})
    public ListResult<Map<String, Object>> getEformDocListFromBox(
            @Parameter(description = "문서함 타입 (01:진행 중 문서함, 02:처리할 문서함, 03:완료 문서함, 04:문서 관리)", required = true) @PathVariable String type,
            @Parameter(description = "문서 제목 및 내용에서 검색할 쿼리 (입력 시, 검색 결과에 해당하는 문서만 표시)") @RequestParam (required = false) String titleAndContent,
            @Parameter(description = "문서 제목에서 검색할 쿼리 (입력 시, 검색 결과에 해당하는 문서만 표시)") @RequestParam (required = false) String title,
            @Parameter(description = "문서 내용에서 검색할 쿼리 (입력 시, 검색 결과에 해당하는 문서만 표시)") @RequestParam (required = false) String content,
            @Parameter(description = "문서 작성일 기준으로 검색할 날짜 범위 (시작)(YYYY-MM-DD)") @RequestParam (required = false) String startCreateDate,
            @Parameter(description = "문서 작성일 기준으로 검색할 날짜 범위 (끝)(YYYY-MM-DD)") @RequestParam (required = false) String endCreateDate,
            @Parameter(description = "문서 처리일 기준으로 검색할 날짜 범위 (시작)(YYYY-MM-DD)") @RequestParam (required = false) String startUpdateDate,
            @Parameter(description = "문서 처리일 기준으로 검색할 날짜 범위 (끝)(YYYY-MM-DD)") @RequestParam (required = false) String endUpdateDate,
            @Parameter(description = "한 번에 표시할 문서 수 (페이징 용)", required = true) @RequestParam(defaultValue = "200") int rowCount,
            @Parameter(description = "목록에서 건너뛰고 표시할 문서 수(1부터 시작)", required = true) @RequestParam(defaultValue = "1") int pageNumber)
    {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Long sCreDate = null;
            Long eCreDate = null;
            Long sUpDate = null;
            Long eUpDate = null;
            String limit = "20";
            String skip = "0";

            List<EformMember> members = eformMemberDAO.selectEformMemberList();
            List<String> memberEnabled = members.stream().filter(f -> Objects.equals(f.getIsEnabled(), "Y")).map(EformMember::getEfId).toList();

            try {
                if (StringUtils.isNotBlank(startCreateDate)) {
                    sCreDate = Long.valueOf(dateFormat.parse(startCreateDate).getTime());
                }
            } catch (ParseException e) {}
            try {
                if (StringUtils.isNotBlank(endCreateDate)) {
                    eCreDate = Long.valueOf(dateFormat.parse(endCreateDate).getTime());
                }
            } catch (ParseException e) {}
            try {
                if (StringUtils.isNotBlank(startUpdateDate)) {
                    sUpDate = Long.valueOf(dateFormat.parse(startUpdateDate).getTime());
                }
            } catch (ParseException e) {}
            try {
                if (StringUtils.isNotBlank(endUpdateDate)) {
                    eUpDate = Long.valueOf(dateFormat.parse(endUpdateDate).getTime());
                }
            } catch (ParseException e) {}

            if (rowCount == 0) rowCount = 200;
            limit = String.valueOf(rowCount);
            skip = String.valueOf((pageNumber - 1) * rowCount);
            Map<String, Object> resultMap = eformService.getDocumentListFromBox(null, type, titleAndContent, title,
                    content, sCreDate, eCreDate, sUpDate, eUpDate, limit, skip, memberEnabled);
            List<Map<String, Object>> docList = new ArrayList<>();
            if ( (resultMap != null) && resultMap.containsKey("documents") ) {
                docList = (List<Map<String, Object>>)resultMap.get("documents");
            }
            return responseService.getListResult(docList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Operation(summary = "eformsign 진행중인 문서 수 조회", description = "eformsign 진행중인 문서 수 조회")
    @RequestMapping(value = "/eformsign/documents/inProgress", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getInProgressDocumentCount(
            @Parameter(description = "담당자 이메일주소") @RequestParam(required = false) String email,
            @Parameter(description = "협력사코드") @RequestParam(required = false) String bpCd)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            resultMap = eformService.getInProgressStatus(email, bpCd);
        } catch (Exception e) {
            resultMap.put("totalCount", Long.valueOf(-1));
        }

        return responseService.getDataResult(resultMap);
    }

    @Operation(summary = "eformsign 문서정보 상세조회 ", description = "eformsign 문서정보 상세조회")
    @RequestMapping(value = "/eformsign/documents/{docId}", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getEformDocumentDetail(
            @Parameter(description = "이폼 문서 ID", required = true) @PathVariable String docId)
    {

        try {
            Map<String, Object> resultMap = eformService.getDocumentDetail(null, docId);
            return responseService.getDataResult(resultMap);
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }


    @Operation(summary = "eformsign 문서 등록(테스트중) ", description = "eformsign 문서 등록 ")
    @RequestMapping(value = "/eformsign/createDoc", method = {RequestMethod.POST})
    public DataResult<Map<String, Object>> createEformDoc(
            @Parameter(description = "템플릿ID", required = true) @RequestParam String temp_id)
    {
        try {
            Map<String, Object> resultMap = eformService.createDocument(null, temp_id);
            return responseService.getDataResult(resultMap);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "eformsign 그룹 목록 ", description = "eformsign 그룹 목록 ")
    @RequestMapping(value = "/eformsign/groups", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getEformGroupList(
            @Parameter(description = "전체 조회여부", required = true) @RequestParam(defaultValue = "false") boolean includeMember,
            @Parameter(description = "커스텀 필트 포맷 정보 포함여부", required = true) @RequestParam(defaultValue = "false") boolean includeField)
    {
        try {
            Map<String, Object> resultMap = eformService.getGroups(null, includeMember, includeField);
            return responseService.getDataResult(resultMap);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "eformsign 멤버 목록 ", description = "eformsign 멤버 목록 ")
    @RequestMapping(value = "/eformsign/members", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getEformMemberList(
            @Parameter(description = "전체 조회여부", required = true) @RequestParam(defaultValue = "false") boolean memberAll,
            @Parameter(description = "커스텀 필트 포맷 정보 포함여부", required = true) @RequestParam(defaultValue = "false") boolean includeField,
            @Parameter(description = "삭제멤버 조회여부", required = true) @RequestParam(defaultValue = "false") boolean includeDelete,
            @Parameter(description = "검색할 회원명 또는 계정ID") @RequestParam(required = false) String searchName)
    {
        try {
            Map<String, Object> resultMap = eformService.getMembers(null, memberAll, includeField, includeDelete, searchName);
            eformService.loadEformMembers(resultMap);
            return responseService.getDataResult(resultMap);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }

    @Operation(summary = "eformsign 서비스 이용가능 여부", description = "eformsign 멤버의 상태를 체크하여 서비스 이용가능 여부를 체크")
    @RequestMapping(value = "/eformsign/available", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> isAvailable(
            @Parameter(description = "등록 이메일주소", required = true) @RequestParam String email)
    {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            boolean isAvailable = eformService.isAvailableService(email);
            if (!isAvailable) {
                Map<String, Object> resultMember = eformService.getMembers(null, true, false, false, null);
                eformService.loadEformMembers(resultMember);
                isAvailable = eformService.isAvailableService(email);
            }

            resultMap.put("isAvailable", Boolean.valueOf(isAvailable));
            return responseService.getDataResult(resultMap);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(), null);
        }
    }


}
