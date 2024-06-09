package com.guroomsoft.icms.withsign.service;

import com.guroomsoft.icms.withsign.wrapper.WithSignAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignService {

    /**
     * 인증토큰
     * @return
     */

    public Map<String, Object> getAccessToken()
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            JSONObject jo = WithSignAPI.getAccessToken();
            if (jo == null) {
                return null;
            } else {
                resultMap = WithSignAPI.getMapFromJsonObject(jo);
                log.debug("{}", resultMap);
                return resultMap;
            }
        } catch (Exception e) {
            log.error(">>>> getAccessToken ::: {} ", e.getMessage());
        }
        return null;
    }

    /**
     * 그룹 목록
     * @return
     */
    public Map<String, Object> getGroup()
    {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            JSONObject jo = WithSignAPI.getGroup();
            if (jo == null) {
                return null;
            } else {
                resultMap = WithSignAPI.getMapFromJsonObject(jo);
                log.debug("{}", resultMap);
                return resultMap;
            }
        } catch (Exception e) {
            log.error(">>>> getAccessToken ::: {} ", e.getMessage());
        }
        return null;
    }


    public Map<String, Object> getServiceUser(Integer page, Integer page_size, Integer group_id)
    {
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (page != null && page.intValue() > 0) {
                params.put("page", page);
            } else {
                params.put("page", Integer.valueOf(1));
            }

            if (page_size != null && page_size.intValue() > 0) {
                params.put("page_size", page_size);
            } else {
                params.put("page_size", Integer.valueOf(20));
            }

            if ( group_id != null &&  group_id.intValue() > -1)  params.put("group_id", Integer.valueOf(group_id));

            params.put("sort", "created_date|desc");

            JSONObject jo = WithSignAPI.getServiceUser(params);
            if (jo == null) {
                return null;
            } else {
                resultMap = WithSignAPI.getMapFromJsonObject(jo);
                log.debug("{}", resultMap);
                return resultMap;
            }
        } catch (Exception e) {
            log.error(">>>> getAccessToken ::: {} ", e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getFormList(Integer page, Integer page_size, String title, Integer group_id)
    {
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            if (page != null && page.intValue() > 0) {
                params.put("page", page);
            } else {
                params.put("page", Integer.valueOf(1));
            }

            if (page_size != null && page_size.intValue() > 0) {
                params.put("page_size", page_size);
            } else {
                params.put("page_size", Integer.valueOf(20));
            }

            if (StringUtils.isNotBlank(title)) {
                params.put("title", title);
            }

            if ( group_id != null &&  group_id.intValue() > -1)  params.put("group_id", Integer.valueOf(group_id));

            params.put("sort", "created_date|desc");

            JSONObject jo = WithSignAPI.getFormList(params);
            if (jo == null) {
                return null;
            } else {
                resultMap = WithSignAPI.getMapFromJsonObject(jo);
                log.debug("{}", resultMap);
                return resultMap;
            }
        } catch (Exception e) {
            log.error(">>>> getAccessToken ::: {} ", e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getFormDetail(String formId)
    {
        Map<String, Object> params = new LinkedHashMap<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            params.put("form_id", formId);

            JSONObject jo = WithSignAPI.getFormDetail(params);
            if (jo == null) {
                return null;
            } else {
                resultMap = WithSignAPI.getMapFromJsonObject(jo);
                log.debug("{}", resultMap);
                return resultMap;
            }
        } catch (Exception e) {
            log.error(">>>> getAccessToken ::: {} ", e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getFormPreview(String formId)
    {
        Map<String, Object> params = new LinkedHashMap<>();
        List<Map<String, String>> fields = new ArrayList<>();
        Map<String, Object> resultMap = new LinkedHashMap<>();
        try {
            params.put("form_id", formId);
            Map<String, String> ceoNm = new LinkedHashMap<>();
            ceoNm.put("id", "6620751baebd412b15000002");
            ceoNm.put("value", "홍길동");
            fields.add(ceoNm);

            Map<String, String> img = new LinkedHashMap<>();
            ceoNm.put("id", "66207566aebd412b15000003");
            ceoNm.put("value", "data:image/gif;base64,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");

            params.put("items", fields);


            JSONObject jo = WithSignAPI.getFormPreview(params);
            if (jo == null) {
                return null;
            } else {
                resultMap = WithSignAPI.getMapFromJsonObject(jo);
                log.debug("{}", resultMap);
                return resultMap;
            }
        } catch (Exception e) {
            log.error(">>>> getAccessToken ::: {} ", e.getMessage());
        }
        return null;
    }


}
