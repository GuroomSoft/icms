package com.guroomsoft.icms.common.filter;

import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.util.AppContant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

@Slf4j
public class UserRequestWrapper extends HttpServletRequestWrapper {
    private HashMap<String, Object> parameterMap;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public UserRequestWrapper(HttpServletRequest request) {
        super(request);
        this.parameterMap = new HashMap<String, Object>(request.getParameterMap());
    }

    @Override
    public String getParameter(String name) {
        String returnValue = null;
        String[] paramArray = getParameterValues(name);
        if ( (paramArray != null)  && (paramArray.length > 0)) {
            returnValue = paramArray[0];
        }

        return returnValue;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.parameterMap.keySet());
    }

    @Override
    public Map getParameterMap() {
        return Collections.unmodifiableMap(this.parameterMap);
    }


    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] temp = (String[]) this.parameterMap.get(name);

        if (temp != null) {
            result = new String[temp.length];
            System.arraycopy(temp, 0, result, 0, temp.length);
        }

        return result;
    }

    public void setParameter(String name, String value) {
        String[] oneParam = {value};
        setParameter(name, oneParam);
    }

    public void setParameter(String name, String[] values) {
        this.parameterMap.put(name, values);
    }

    public void setRequestAccountId(String accountId) {
        setParameter(AppContant.REQ_PARAM_ACCOUNT_ID, StringUtils.defaultString(accountId));
    }

    public void setRequestUserUid(String userUid) {
        setParameter(AppContant.REQ_PARAM_USER_UID, userUid);
    }

    public void setRequestUserInfo(@NotNull User user) {
        // 이후 추가적인 요청 파라미터를 추가
        setParameter(AppContant.REQ_PARAM_USER_UID, String.valueOf(user.getUserUid()));
        setParameter(AppContant.REQ_PARAM_ACCOUNT_ID, user.getAccountId());
        setParameter(AppContant.REQ_PARAM_CORP_CD, user.getCorpCd());
        setParameter(AppContant.REQ_PARAM_PLANT_CD, StringUtils.defaultString(user.getPlantCd()));
        setParameter(AppContant.REQ_PARAM_ORG_CD, StringUtils.defaultString(user.getOrgCd()));
        setParameter(AppContant.REQ_PARAM_TEAM_CD, StringUtils.defaultString(user.getTeamCd()));
        setParameter(AppContant.REQ_PARAM_BP_CD, StringUtils.defaultString(user.getBpCd()));
    }
}
