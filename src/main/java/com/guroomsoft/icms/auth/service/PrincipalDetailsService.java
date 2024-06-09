package com.guroomsoft.icms.auth.service;

import com.guroomsoft.icms.auth.dao.UserDAO;

import com.guroomsoft.icms.auth.dto.PrincipalDetails;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.dto.UserRole;
import com.guroomsoft.icms.biz.code.dao.CorporationDAO;
import com.guroomsoft.icms.biz.code.dao.PartnerDAO;
import com.guroomsoft.icms.biz.code.dao.PlantDAO;
import com.guroomsoft.icms.biz.code.dto.Corporation;
import com.guroomsoft.icms.biz.code.dto.Partner;
import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    private static final String ROLE_PREFIX = "ROLE_";

    private final UserDAO userDAO;
    private final PlantDAO plantDAO;
    private final PartnerDAO partnerDAO;

    private final CorporationDAO corporationDAO;

    @Override
    public PrincipalDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("👉 [PrincipalDetailsService] loadUserByUsername");
        PrincipalDetails userDetails = new PrincipalDetails();
        // 사용자 정보 조회
        User user = null;
        try {
            user = userDAO.selectUserById(username);
            if ( StringUtils.isNotBlank(user.getCorpCd()) )
            {
                Corporation corpInfo = corporationDAO.selectCorporationByKey(user.getCorpCd());
                if (corpInfo != null) user.setCorporationDetail(corpInfo);
            }

            if ( StringUtils.isNotBlank(user.getPlantCd()) )
            {
                Plant userPlant = plantDAO.selectPlantByKey(user.getPlantCd());
                if (userPlant != null) user.setPlantDetail(userPlant);
            }

            if ( StringUtils.isNotBlank(user.getBpCd()) )
            {
                Partner bpInfo = partnerDAO.selectByKey(user.getBpCd());
                if (bpInfo != null) user.setPartnerDetail(bpInfo);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (user == null) {
            throw new UsernameNotFoundException(MessageUtil.getMessage("error.user.notfound", new String[]{username}));
        }

        // 권한 정보 조회
        List<UserRole> roles = null;
        try {
            roles = userDAO.selectUserRoleById(username);
            if (roles != null)  user.setAuthorities(roles);

            userDetails.setUser(user);
        } catch (Exception e) {
            log.error("👉 [PrincipalDetailsService] role 정보조회 에러");
            log.error(e.getMessage());
        }

        // TODO 사용자 그룹 및 메뉴정보

        // TODO 계정 상태 정보 체크


        return userDetails;
    }

}
