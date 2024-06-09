package com.guroomsoft.icms.http;

import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.util.AppContant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class GrRequestWrapper extends HttpServletRequestWrapper {
    private HttpServletRequest wrappedReq;
    private HashMap<String, String[]> parameterMap;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public GrRequestWrapper(HttpServletRequest request) {
        super(request);
        initWrapper(request);
    }

    public void addParameter(String name, String value) {
        if (parameterMap == null) {
            parameterMap = new HashMap<String, String[]>();
            parameterMap.putAll(wrappedReq.getParameterMap());
        }

        String[] values = parameterMap.get(name);
        if (values == null) {
            values = new String[0];
        }

        List<String> list = new ArrayList<String>(values.length + 1);
        list.addAll(Arrays.asList(values));
        list.add(value);
        parameterMap.put(name, list.toArray(new String[0]));
    }
    @Override
    public Map getParameterMap() {
        if (parameterMap == null) {
            return wrappedReq.getParameterMap();
        }

        return Collections.unmodifiableMap(parameterMap);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (parameterMap == null) {
            return wrappedReq.getParameterNames();
        }
        return Collections.enumeration(this.parameterMap.keySet());
    }

    @Override
    public String getParameter(String name) {
        if (parameterMap == null) {
            return wrappedReq.getParameter(name);
        }

        String[] strings = parameterMap.get(name);
        if (strings != null) {
            return strings[0];
        }

        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        if (parameterMap == null) {
            return wrappedReq.getParameterValues(name);
        }
        return parameterMap.get(name);
    }

    private void initWrapper(HttpServletRequest request) {
        this.wrappedReq = request;
    }

    public void setParameter(String name, String value) {
        String[] oneParam = {value};
        setParameter(name, oneParam);
    }

    public void setParameter(String name, String[] values) {
        this.parameterMap.put(name, values);
    }

    public void setRequestAccountId(String accountId) {
        addParameter(AppContant.REQ_PARAM_ACCOUNT_ID, StringUtils.defaultString(accountId));
    }

    public void setRequestUserUid(Long userUid) {
        addParameter(AppContant.REQ_PARAM_USER_UID, StringUtils.defaultString(String.valueOf(userUid), "0"));
    }
    public void setRequestOrgCd(String orgCd) {
        addParameter(AppContant.REQ_PARAM_ORG_CD, StringUtils.defaultString(orgCd));
    }

    public void setRequestBpCd(String bpCd) {
        addParameter(AppContant.REQ_PARAM_BP_CD, StringUtils.defaultString(bpCd));
    }

    public void setRequestAdditionalUserInfo(User user) {
        addParameter(AppContant.REQ_PARAM_USER_UID, String.valueOf(user.getUserUid()));
        addParameter(AppContant.REQ_PARAM_ACCOUNT_ID, user.getAccountId());
        addParameter(AppContant.REQ_PARAM_ORG_CD, user.getOrgCd());
        addParameter(AppContant.REQ_PARAM_BP_CD, user.getBpCd());
    }
}
