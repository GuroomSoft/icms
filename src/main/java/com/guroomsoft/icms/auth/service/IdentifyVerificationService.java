package com.guroomsoft.icms.auth.service;

import com.guroomsoft.icms.auth.dao.IdentifyVerficationDAO;
import com.guroomsoft.icms.auth.dto.IdentifyVerification;
import com.guroomsoft.icms.common.dao.AppConfigDAO;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentifyVerificationService {
    private final IdentifyVerficationDAO identifyVerficationDAO;
    private final AppConfigService appConfigService;

    // 인증코드 등록
    @Transactional
    public IdentifyVerification createIdentifyVerification(IdentifyVerification data) {
        try {
            int count = identifyVerficationDAO.insertIdentifyVerification(data);
            if (count == 1) {
                return data;
            }
        } catch (Exception e) {
            log.error("인증코드 등록 실패하였습니다.");
        }
        return null;
    }

    @Transactional(readOnly = true)
    public IdentifyVerification findIdentifyVerification(IdentifyVerification params) {
        try {
            return identifyVerficationDAO.selectIdentifyVerification(params);
        } catch (Exception e) {
            log.error("인증코드 정보 조회 실패");
        }
        return null;
    }

    @Transactional(readOnly = true)
    public IdentifyVerification findIdentifyVerificationById(final String accountId) {
        IdentifyVerification params = new IdentifyVerification();
        params.setAccountId(accountId);
        try {
            return identifyVerficationDAO.selectIdentifyVerification(params);
        } catch (Exception e) {
            log.error("인증코드 정보 조회 실패");
        }
        return null;
    }


    // 인증코드 확인
    @Transactional(readOnly = true)
    public boolean checkIdentifyVerification(IdentifyVerification data) {
        try {
            // 1. 발행 정보 조회
            IdentifyVerification certInfo = findIdentifyVerificationById(data.getAccountId());
            if (certInfo == null) {
                return false;
            }

            if (DateTimeUtil.isBefore(certInfo.getExpiredDt())) {
                // 인증만료 시간 초과
                return false;
            }

            if (!data.getCertCode().equals(certInfo.getCertCode())) {
                // 인증 코드 다름
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
