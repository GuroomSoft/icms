package com.guroomsoft.icms.biz.code.service;

import com.guroomsoft.icms.biz.code.dao.CorporationDAO;
import com.guroomsoft.icms.biz.code.dao.PlantDAO;
import com.guroomsoft.icms.biz.code.dto.Corporation;
import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.util.AppContant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorporationService {
    private final CorporationDAO corporationDAO;
    private final PlantDAO plantDAO;
    /**
     * 회사 목록
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<Corporation> findCorporation(Corporation cond) throws Exception {
        try {
            List<Corporation> resultSet = corporationDAO.selectCorporation(cond);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            log.error("{} findCorporationDAO {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    /**
     * 회사 상세조회
     * @param corpCd
     * @return
     */
    @Transactional(readOnly = true)
    public Corporation findCorporationByKey(String corpCd)  throws Exception
    {
        if (StringUtils.isBlank(corpCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            Corporation resultSet = corporationDAO.selectCorporationByKey(corpCd);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                Plant cond = new Plant();
                cond.setCorpCd(resultSet.getCorpCd());
                List<Plant> plantList = plantDAO.selectPlant(cond);
                resultSet.setPlants(plantList);
                return resultSet;
            }
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }

    /**
     * 회사 등록
     * @param data
     * @return
     */
    @Transactional
    public Corporation create(Corporation data, Long reqUserUid) throws Exception
    {
        if (!isValid(data)) {
            throw new CInvalidArgumentException();
        }

        if (isExist(data.getCorpCd())) {
            throw new CAlreadyExistException();
        }
        data.setRegUid(reqUserUid);
        if (StringUtils.isBlank(data.getUseAt())) data.setUseAt(AppContant.CommonValue.YES.getValue());

        int inserted = 0;
        try {
            inserted = corporationDAO.insertCorporation(data);
            if (inserted == 0) {
                return null;
            } else {
                return corporationDAO.selectCorporationByKey(data.getCorpCd());
            }
        } catch (Exception e) {
            throw new CRegistrationFailException();
        }
    }

    /**
     * 유효성 검사
     * @param data
     * @return
     */
    private boolean isValid(Corporation data)
    {
        if (data == null
                || StringUtils.isBlank(data.getCorpCd())
                || StringUtils.isBlank(data.getCorpCountry())
                || StringUtils.isBlank(data.getCorpNm()) )
        {
            return false;
        }

        return true;
    }

    /**
     * 중복체크
     * @param corpCd
     * @return
     */
    private boolean isExist(String corpCd) throws CInvalidArgumentException
    {
        if (StringUtils.isBlank(corpCd))
        {
            throw new CInvalidArgumentException();
        }

        try {
            Corporation d = corporationDAO.selectCorporationByKey(corpCd);
            if (d != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 회사 정보 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modify(Corporation data, Long reqUserUid) throws Exception
    {
        if (data == null || StringUtils.isBlank(data.getCorpCd())) {
            throw new CInvalidArgumentException();
        }
        try {
            data.setModUid(reqUserUid);
            return corporationDAO.updateCorporation(data);
        } catch (Exception e) {
            throw new CModifictionFailException();
        }
    }

    /**
     * 회사 및 플래트 정보 삭제
     * @param corpCd
     * @return
     * @throws Exception
     */
    @Transactional
    public int remove(String corpCd)  throws Exception
    {
        if (StringUtils.isBlank(corpCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            int deleted = corporationDAO.deleteCorporation(corpCd);
            if (deleted > 0)
            {
                plantDAO.deletePlant(corpCd, null);
            }
            return deleted;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CRemoveFailException();
        }
    }

    @Transactional(readOnly = true)
    public String getName(String corpCd)
    {
        try {
            Corporation data = corporationDAO.selectCorporationByKey(corpCd);
            if (data != null) {
                return StringUtils.defaultString(data.getCorpNm());
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

}
