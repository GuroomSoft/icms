package com.guroomsoft.icms.common.service;


import com.guroomsoft.icms.common.dao.AppConfigDAO;
import com.guroomsoft.icms.common.dto.AppConfig;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppConfigService {
    public static final String DEFAULT_USER_GROUP = "default-user-group";
    public static final String AUTH_DURATION = "auth-duration";
    public static final String DEFAULT_USER_ROLE = "default-user-role";         // 디폴트 사용자 역할(일반사용자)
    public static final String DEFAULT_INIT_PWD = "default-init-pwd";           // 디폴트 초기화 암호
    public static final String ROWCOUNT_PER_PAGE = "rowcount-per-page";         // 일반 페이지당 건수
    public static final String FERROUS_ITEM_LIST = "ferrous-item-list";         // 대시보드용 항목 리스트

    public static final String DEFAULT_ROWCOUNT_PER_PAGE = "20";                // 디폴트 페이지당 건수
    private final AppConfigDAO appConfigDAO;

    /**
     * 시스템 구성 설정 목록 조회
     * @param cond
     * @return
     */
    public List<AppConfig> findAppConfig(AppConfig cond) {
        List<AppConfig> resultSet = new ArrayList<>();
        try {
            resultSet = appConfigDAO.selectConfig(cond);
            return resultSet;
        } catch (Exception e) {
            log.error("findAppConfig {}", e.getMessage());
        }
        return null;
    }

    public String findAppConfigByKey(final String envKey) {
        try {
            return appConfigDAO.selectValueByKey(envKey);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 시스템 구성 설정값 저장
     * @param data
     * @return
     */
    @Transactional
    public AppConfig saveAppConfig(@NonNull AppConfig data) {
        // 유효성체크
        if (StringUtils.isBlank(data.getEnvKey())) {
            throw new CRequiredException("구성 키는 필수항목입니다.");
        } else if (StringUtils.isBlank(data.getEnvValue())) {
            throw new CRequiredException("구성 값은 필수항목입니다.");
        } else if (StringUtils.isBlank(data.getCategory())) {
            throw new CRequiredException("분류는 필수항목입니다.");
        }

        try {
            int insCount = appConfigDAO.saveConfig(data);
            if (insCount > 0) {
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }


    public int getIntValue(final String envKey) throws Exception {
        String value = StringUtils.defaultString(appConfigDAO.selectValueByKey(envKey), "0");
        if (StringUtils.isNumeric(value)) {
            return Integer.valueOf(value).intValue();
        } else {
            throw new NumberFormatException();
        }
    }

    public long getLongValue(final String envKey) throws Exception {
        String value = StringUtils.defaultString(appConfigDAO.selectValueByKey(envKey), "0");
        if (StringUtils.isNumeric(value)) {
            return Long.valueOf(value).longValue();
        } else {
            throw new NumberFormatException();
        }
    }

    public double getDoubleValue(final String envKey) throws Exception {
        String value = StringUtils.defaultString(appConfigDAO.selectValueByKey(envKey), "0.0");
        if (StringUtils.isNumeric(value)) {
            return Double.valueOf(value).doubleValue();
        } else {
            throw new NumberFormatException();
        }
    }

}
