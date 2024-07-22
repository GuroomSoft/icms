package com.guroomsoft.icms.biz.econtract.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.biz.econtract.dao.EformMemberDAO;
import com.guroomsoft.icms.biz.econtract.dto.EformMember;
import com.guroomsoft.icms.biz.econtract.wrapper.EformAPI;
import com.guroomsoft.icms.common.exception.CInvalidArgumentException;
import com.guroomsoft.icms.util.AppContant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EformService {
    private final EformMemberDAO eformMemberDAO;
    private final UserDAO userDAO;
    private final EformAPI eformAPI;

    /**
     * register eform member data to db
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int createEformMember(EformMember data) throws Exception
    {
        if (data == null || StringUtils.isBlank(data.getEfId())) {
            return -1;
        }
        return eformMemberDAO.insertEformMember(data);
    }

    /**
     * delete eform member from DB
     * @param efId
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeEformMember(String efId) throws Exception
    {
        return eformMemberDAO.deleteEformMember(efId);
    }

    /**
     * Load to DB eform member user data from eformsign service
     * @param members
     */
    @Transactional
    public void loadEformMembers(Map<String, Object> members)
    {
        log.debug(members.toString());

        if (!members.containsKey("members")) {
            return;
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) members.get("members");
        try {
            // 이전 멤버정보 초기화
            eformMemberDAO.deleteEformMember(null);
            for (Map<String, Object> item : items) {
                EformMember data = new EformMember();
                data.setEfId((String)item.get("id"));
                data.setAccountId((String)item.get("account_id"));
                data.setEfName((String)item.get("name"));
                if ( (item.get("isRefused") != null) && ((Boolean)item.get("isRefused")).booleanValue() )
                {
                    data.setIsRefused(AppContant.CommonValue.YES.getValue());
                } else {
                    data.setIsRefused(AppContant.CommonValue.NO.getValue());
                }
                if ( (item.get("isInvited") != null) && ((Boolean)item.get("isInvited")).booleanValue() )
                {
                    data.setIsInvited(AppContant.CommonValue.YES.getValue());
                } else {
                    data.setIsInvited(AppContant.CommonValue.NO.getValue());
                }
                if ( (item.get("isWithdrawal") != null) && ((Boolean)item.get("isWithdrawal")).booleanValue() )
                {
                    data.setIsWithdrawal(AppContant.CommonValue.YES.getValue());
                } else {
                    data.setIsWithdrawal(AppContant.CommonValue.NO.getValue());
                }
                if ( (item.get("isExpired") != null) && ((Boolean)item.get("isExpired")).booleanValue() )
                {
                    data.setIsExpired(AppContant.CommonValue.YES.getValue());
                } else {
                    data.setIsExpired(AppContant.CommonValue.NO.getValue());
                }
                if ( (item.get("deleted") != null) && ((Boolean)item.get("deleted")).booleanValue() )
                {
                    data.setIsDeleted(AppContant.CommonValue.YES.getValue());
                } else {
                    data.setIsDeleted(AppContant.CommonValue.NO.getValue());
                }
                if ( (item.get("enabled") != null) && ((Boolean)item.get("enabled")).booleanValue() )
                {
                    data.setIsEnabled(AppContant.CommonValue.YES.getValue());
                } else {
                    data.setIsEnabled(AppContant.CommonValue.NO.getValue());
                }

                createEformMember(data);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 이폼 멤버 정보 조회
     * @param email
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public EformMember findMemberByEmail(String email) throws Exception
    {
        if (StringUtils.isBlank(email)) {
            throw new CInvalidArgumentException();
        }

        return eformMemberDAO.selectEformMember(email);
    }

    /**
     * 04시 05분에 멤버정보 로드
     */
    @Transactional
    @Scheduled(cron = "0 5 4 * * *")
    public void scheduleLoadMember()
    {
        try {
            Map<String, Object> resultMember = getMembers(null, true, false, false, null);
            loadEformMembers(resultMember);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 이폼 서비스 이용 가능여부 조회
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean isAvailableService(String email)
    {
        if (StringUtils.isBlank(email)) {
            return false;
        }

        Map<String, Object> cond = new LinkedHashMap<>();
        cond.put("email", email);
        try {
            List<Map<String, Object>> userList = userDAO.getEmployeeUsers(cond);
            if (userList == null || userList.isEmpty()) {
                return false;
            }

            for (Map<String, Object> user : userList) {
                if (((String)user.get("efIsEnabled")).equalsIgnoreCase("Y")) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return false;
    }

    /**
     * 인증토큰조회
     * @param memberId
     * @return
     */
    public Map<String, Object> getAccessToken(String memberId)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> params = new LinkedHashMap<>();
        try {
            JSONObject jo = EformAPI.getAccessToken(memberId);
            if (jo == null) {
                return null;
            } else {
                JSONObject apiKey = (JSONObject) jo.get("api_key");

                resultMap.put("memberId", memberId);
                resultMap.put("name", (String)apiKey.get("name"));
                resultMap.put("companyId", (String)((JSONObject)apiKey.get("company")).get("company_id"));
                resultMap.put("companyName", (String)((JSONObject)apiKey.get("company")).get("name"));
                resultMap.put("apiUrl", (String)((JSONObject)apiKey.get("company")).get("api_url"));

                JSONObject authToken = (JSONObject) jo.get("oauth_token");
                resultMap.put("accessToken", (String)authToken.get("access_token"));
                resultMap.put("refreshToken", (String)authToken.get("refresh_token"));

                log.debug("{}", resultMap);
                return resultMap;
            }
        } catch (Exception e) {
            log.error(">>>> getAccessToken ::: {} ", e.getMessage());
        }
        return null;
    }

    /**
     * 이폼 문서 목록 조회 (사용안함)
     * @param memberId
     * @return
     */
    public Map<String, Object> getDocumentList(String memberId)
    {
        Map<String, Object> authInfo = getAccessToken(memberId);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("type", "01");
        params.put("title_and_content", "");
        params.put("title", "");
        params.put("content", "");
        params.put("limit", "20");
        params.put("skip", "1");

        JSONObject resultJson = EformAPI.getDocumentList(authInfo, params);
        return EformAPI.getMapFromJsonObject(resultJson);
    }

    /**
     * 문서함의 문서목록 조회
     * @param memberId
     * @param type
     * @param titleAndContent
     * @param title
     * @param content
     * @param startCreateDate
     * @param endCreateDate
     * @param startUpdateDate
     * @param endUpdateDate
     * @param limit
     * @param skip
     * @return
     */
    public Map<String, Object> getDocumentListFromBox(
            String memberId,
            String type, String titleAndContent, String title, String content,
            Long startCreateDate, Long endCreateDate,
            Long startUpdateDate, Long endUpdateDate,
            String limit, String skip, List<String> members)
    {
        Map<String, Object> combinedResult = new LinkedHashMap<>();

        //Map<String, Object> authInfo = getAccessToken(memberId);
        for (String member : members) {
            Map<String, Object> authInfo = getAccessToken(member);
            if(authInfo == null)
            {
                continue;
            }
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("type", type);
            params.put("title_and_content", StringUtils.defaultString(titleAndContent));
            params.put("title", StringUtils.defaultString(title));
            params.put("content", StringUtils.defaultString(content));
            if (startCreateDate != null && startCreateDate.longValue() > 0)
                params.put("start_create_date", startCreateDate);
            if (endCreateDate != null && endCreateDate.longValue() > 0) params.put("end_create_date", endCreateDate);
            if (startUpdateDate != null && startUpdateDate.longValue() > 0)
                params.put("start_update_date", startUpdateDate);
            if (endUpdateDate != null && endUpdateDate.longValue() > 0) params.put("end_update_date", endUpdateDate);
            params.put("limit", StringUtils.defaultString(limit));
            params.put("skip", StringUtils.defaultString(skip));

            JSONObject resultJson = eformAPI.getDocumentListFromBox(authInfo, params);

            Map<String, Object> resultMap = EformAPI.getMapFromJsonObject(resultJson);
            resultMap.forEach((key, value) -> {
                if (combinedResult.containsKey(key)) {
                    // Handle merging of duplicate keys if necessary
                    // For simplicity, we're assuming values are lists here
                    List<Object> combinedList = new ArrayList<>();
                    if (combinedResult.get(key) instanceof List) {
                        combinedList.addAll((List<?>) combinedResult.get(key));
                    } else {
                        combinedList.add(combinedResult.get(key));
                    }
                    if (value instanceof List) {
                        combinedList.addAll((List<?>) value);
                    } else {
                        combinedList.add(value);
                    }
                    combinedResult.put(key, combinedList);
                } else {
                    combinedResult.put(key, value);
                }
            });

            //JSONObject resultJson = eformAPI.getDocumentListFromBox(authInfo, params);
        }
        return EformAPI.getMapFromJsonObject(new JSONObject(combinedResult));
        //return EformAPI.getMapFromJsonObject(resultJson);
    }

    // 현재 처리중인 문서 통계 정보 담당자용
    public Long getDocumentInfoFromBox(String memberId, String type, String bpCd)
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        Map<String, Object> authInfo = getAccessToken(memberId);
        // 01:진행 중 문서함, 02:처리할 문서함
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("type", type);
        params.put("title", StringUtils.defaultString(bpCd));
        params.put("limit", "2");
        params.put("skip", "0");

        JSONObject resultJson = eformAPI.getDocumentListFromBox(authInfo, params);
        if (resultJson == null) {
            return -1L;
        }

        return (Long)resultJson.getOrDefault("total_rows", 0L);
    }

    /**
     * 전체 진행중인 문서함 카운트 조회
     * @param bpCd
     * @return
     * @throws Exception
     */
    public Map<String, Object> getInProgressStatus(String myEmail, String bpCd) throws Exception
    {
        int totalCount = 0;

        Map<String, Object> resultMap = new LinkedHashMap<>();

        List<EformMember> members = eformMemberDAO.selectEformMemberList();
        if (members == null) {
            return null;
        }

        for (EformMember member : members)
        {
            Long cnt = getDocumentInfoFromBox(member.getAccountId(), "01", bpCd);
            totalCount = totalCount + cnt.intValue();
            if (member.getAccountId().equalsIgnoreCase(StringUtils.defaultString(myEmail))) {
                resultMap.put("myCount", cnt);
            }
        }

        resultMap.put("totalCount", Long.valueOf(totalCount));
        return resultMap;
    }

    /**
     * 문서 상세정보 조회
     * @param memberId
     * @param docId
     * @return
     */
    public Map<String, Object> getDocumentDetail(String memberId, String docId) throws Exception
    {
        if (StringUtils.isBlank(docId)) {
            throw new CInvalidArgumentException();
        }

        Map<String, Object> authInfo = getAccessToken(memberId);
        JSONObject resultJson = eformAPI.getDocumentDetail(authInfo, docId);
        return EformAPI.getMapFromJsonObject(resultJson);
    }

    /**
     * 이폼사인 멤버조회
     * @param memberAll
     * @param includeField
     * @param includeDelete
     * @param searchName
     * @return
     */
    public Map<String, Object> getMembers(String memberId,
                                          boolean memberAll,
                                          boolean includeField,
                                          boolean includeDelete,
                                          String searchName)
    {
        Map<String, Object> authInfo = getAccessToken(memberId);
        Map<String, Object> queryMap = new LinkedHashMap<>();
        queryMap.put("member_all", Boolean.valueOf(memberAll));
        queryMap.put("include_field", Boolean.valueOf(includeField));
        queryMap.put("include_delete", Boolean.valueOf(includeDelete));
        if ( StringUtils.isNotBlank(searchName) )
        {
            queryMap.put("eb_name_search", searchName);
        }

        JSONObject resultJson = eformAPI.getMembers(authInfo, queryMap);
        if (resultJson != null) {
            return EformAPI.getMapFromJsonObject(resultJson);
        } else {
            return null;
        }
    }

    /**
     * 그룹목록 조회
     * @param memberId
     * @param includeMember
     * @param includeField
     * @return
     */
    public Map<String, Object> getGroups(String memberId,
                                         boolean includeMember,
                                         boolean includeField)
    {
        Map<String, Object> authInfo = getAccessToken(memberId);
        Map<String, Object> queryMap = new LinkedHashMap<>();
        queryMap.put("include_member", Boolean.valueOf(includeMember));
        queryMap.put("include_field", Boolean.valueOf(includeField));

        JSONObject resultJson = eformAPI.getGroups(authInfo, queryMap);
        if (resultJson != null) {
            return EformAPI.getMapFromJsonObject(resultJson);
        } else {
            log.debug("👉 그룹 목록 조회 결과 = NULL");
            return null;
        }
    }

    /**
     * 템플릿 목록 조회
     * @param memberId
     * @return
     */
    public Map<String, Object> getForm(String memberId)
    {
        Map<String, Object> authInfo = getAccessToken(memberId);
        JSONObject resultJson = eformAPI.getForms(authInfo);
        return EformAPI.getMapFromJsonObject(resultJson);
    }

    /**
     * 문서 등록
     * @param memberId
     * @param templateId
     * @return
     */
    public Map<String, Object> createDocument(String memberId, String templateId)
    {
        Map<String, Object> authInfo = getAccessToken(memberId);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("template_id", templateId);

        JSONObject content = generateDocContent();

        JSONObject resultJson = null;
        try {
            resultJson = EformAPI.createDocument2(authInfo, params, content);
        } catch (IOException e) {
            log.error(">>>>> 문서 생성 에러 발생");
        }
        return EformAPI.getMapFromJsonObject(resultJson);
    }

    public JSONObject generateDocContent()
    {
        JSONObject doc = new JSONObject();
        JSONObject content = new JSONObject();
        JSONArray notification = new JSONArray();


        content.put("document_name", "가격합의서_테스트");
        content.put("comment", "코멘트를 작성합니다.");

        JSONArray recipients = getRecipients();
        content.put("recipients", recipients);

        JSONArray fields = getFields();
        content.put("fields", fields);

        content.put("notification", notification);

        doc.put("document", content);

        return doc;
    }

    /**
     * 다음 단계 리스트로 단계 객체의 Array(step_type, id, name 등)를 포함한다. 값이 없으면 문서를 임시 저장한다.
     * @return
     */
    private JSONArray getRecipients()
    {
        JSONArray recipients = new JSONArray();

        JSONObject jo = new JSONObject();
        jo.put("step_type", "05");  // 공통: "01"(완료) 신형 워크플로우: "05"(참여자), "06"(검토자)
        jo.put("use_mail", Boolean.FALSE);  // (신형 워크플로우) 이메일 알림 사용 여부
        jo.put("use_sms",  Boolean.FALSE);  // (신형 워크플로우) SMS 알림 사용 여부
        jo.put("is_noti_ignore",  Boolean.FALSE);  // 알림 사용 여부 검토자, 참여자 단계용 (신형 워크플로우) 옵션을 적용하지 않는 경우 기본값은 false
        JSONObject member = getMember();    //(신형 워크플로우) 수신자 정보
        jo.put("member", member);
//        JSONObject group = new JSONObject(); //(신형 워크플로우) 그룹 정보 그룹 목록 조회 API를 통해 확인할 수 있는 그룹 ID를 입력해야 한다.
        // group.put("id", "groupId");
//        jo.put("group", group);
        // jo.put("business_num", null);   // 법인 공동인증서 확인용 사업자등록번호
        JSONObject authJson = getAuth(); // 인증 정보 및 문서 전송 기간 정보
        jo.put("auth", authJson);
        recipients.add(jo);
        return recipients;
    }

    private JSONObject getAuth()
    {
        JSONObject authJson = new JSONObject(); // 인증 정보 및 문서 전송 기간 정보
        authJson.put("password", "6789");
        authJson.put("password_hint", "휴대폰번호 뒷자리를 입력해주세요");

        JSONObject validJson = new JSONObject();    // 문서 전송 기한
        validJson.put("day", 7);
        validJson.put("hour", 0);

        authJson.put("valid", validJson);
        return authJson;
    }
    private JSONObject getMember()
    {
        JSONObject jo = new JSONObject();
        jo.put("name", "참여_홍길동"); // (신형 워크플로우) 수신자 이름
        jo.put("id", "sjnam@guroomsoft.co.kr");   // (신형 워크플로우) 수신자 ID (멤버일 경우) 혹은 이메일 주소(멤버/외부자 무관)
        JSONObject sms = new JSONObject();
        sms.put("country_code", "+82");
        sms.put("phone_number", "01062274622");

        jo.put("sms", sms); // (신형 워크플로우) 수신자 휴대폰 정보
        return jo;
    }
    /**
     * 문서 내 입력항목에 작성할 데이터 리스트
     * @return
     */
    private JSONArray getFields()
    {
        JSONArray fields = new JSONArray();
        JSONObject f1 = new JSONObject();
        f1.put("id", "lbl_body");
        f1.put("value", "요약 본문이 들어가는 곳");
        fields.add(f1);

        JSONObject f2 = new JSONObject();
        f2.put("id", "emp_nm");
        f2.put("value", "담당명_홍길동");
        fields.add(f2);

        return fields;
    }

    /**
     * 문서 내 폼 패러미터로 작성할 데이터 리스트
     * @return
     */
    private JSONArray getParameters()
    {
        JSONArray parameters = new JSONArray();

        return parameters;
    }

    /**
     * 이폼 문서 삭제
     * @param memberId
     * @return
     */
    public Map<String, Object> delDocuments(String memberId, List<String> documentsIds)
    {
        Map<String, Object> authInfo = getAccessToken(memberId);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("document_ids", documentsIds);

        JSONObject resultJson = EformAPI.delDocuments(authInfo, params);
        return EformAPI.getMapFromJsonObject(resultJson);
    }

    /**
     * 이폼 문서 취소
     * @param memberId
     * @return
     */
    public Map<String, Object> cancelDocuments(String memberId, List<String> documentsIds)
    {
        Map<String, Object> authInfo = getAccessToken(memberId);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("document_ids", documentsIds);

        Map<String, Object> input = new LinkedHashMap<>();
        input.put("input", params);
        input.put("comment", "문서 취소");

        JSONObject resultJson = EformAPI.cancelDocuments(authInfo, input);
        return EformAPI.getMapFromJsonObject(resultJson);
    }

}
