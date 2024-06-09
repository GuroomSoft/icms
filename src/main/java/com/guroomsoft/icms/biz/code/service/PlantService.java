package com.guroomsoft.icms.biz.code.service;

import com.guroomsoft.icms.biz.code.dao.PlantDAO;
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
public class PlantService {
    private final PlantDAO plantDAO;
    /**
     * 플랜드 목록
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<Plant> findPlant(Plant cond) throws Exception {
        try {
            List<Plant> resultSet = plantDAO.selectPlant(cond);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            log.error("{} findPlant {}", this.getClass().getSimpleName(), e.getMessage());
        }
        return null;
    }

    /**
     * 플랜트 상세조회
     * @param plantCd
     * @return
     */
    @Transactional(readOnly = true)
    public Plant findPlantByKey(String plantCd)  throws Exception
    {
        if (StringUtils.isBlank(plantCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            Plant resultSet = plantDAO.selectPlantByKey(plantCd);
            if (resultSet == null) {
                throw new CNotFoundException();
            } else {
                return resultSet;
            }
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }

    /**
     * 명칭조회
     * @param plantCd
     * @return
     */
    @Transactional(readOnly = true)
    public String getName(String plantCd)
    {
        if (StringUtils.isBlank(plantCd)) {
            return "";
        }

        try {
            Plant org = plantDAO.selectPlantByKey(plantCd);
            if (org != null) {
                return StringUtils.defaultString(org.getPlantNm());
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }


    @Transactional
    public Plant createPlant(Plant data) throws Exception
    {
        if (!isValid(data)) {
            throw new CInvalidArgumentException();
        }

        if (isExist(data)) {
            throw new CAlreadyExistException();
        }

        if (StringUtils.isBlank(data.getUseAt())) data.setUseAt(AppContant.CommonValue.YES.getValue());

        int inserted = 0;
        try {
            inserted = plantDAO.insertPlant(data);
            if (inserted == 0) {
                return null;
            } else {
                return plantDAO.selectPlantByKey(data.getPlantCd());
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
    private boolean isValid(Plant data)
    {
        if (data == null
                || StringUtils.isBlank(data.getPlantCd())
                || StringUtils.isBlank(data.getPlantNm()) )
        {
            return false;
        }

        return true;
    }

    /**
     * 중복체크
     * @param data
     * @return
     */
    private boolean isExist(Plant data) throws CInvalidArgumentException
    {
        if (data == null || StringUtils.isBlank(data.getPlantCd()))
        {
            throw new CInvalidArgumentException();
        }
        try {
            Plant d = plantDAO.selectPlantByKey(data.getPlantCd());
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
     * Plant 정보 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyPlant(Plant data) throws Exception
    {
        if (data == null || StringUtils.isBlank(data.getPlantCd())) {
            throw new CInvalidArgumentException();
        }
        try {
            return plantDAO.updatePlant(data);
        } catch (Exception e) {
            throw new CModifictionFailException();
        }
    }

    /**
     * Plant 정보 삭제
     * @param plantCd
     * @return
     * @throws Exception
     */
    @Transactional
    public int removePlant(String plantCd)  throws Exception
    {
        if (StringUtils.isBlank(plantCd)) {
            throw new CInvalidArgumentException();
        }

        try {
            return plantDAO.deletePlant(null, plantCd);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CRemoveFailException();
        }
    }

}
