package com.guroomsoft.icms.biz.code.service;

import com.guroomsoft.icms.auth.dto.Group;
import com.guroomsoft.icms.biz.code.dao.CorporationDAO;
import com.guroomsoft.icms.biz.code.dao.PlantDAO;
import com.guroomsoft.icms.biz.code.dto.Corporation;
import com.guroomsoft.icms.biz.code.dto.GroupPlant;
import com.guroomsoft.icms.biz.code.dto.Plant;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.util.AppContant;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
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
    private final PlantService plantService;

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

    /**
     * 플랜트 그룹 목록 조회
     * @param cond
     * @return
     * @throws Exception
     */
    // @Cacheable("plantGroupInfo")
    @Transactional(readOnly = true)
    public List<Group> findPlantGroups(Group cond) throws Exception
    {
        List<Group> data = plantDAO.selectPlantGroups(cond);
        return data;
    }

    /**
     * PK 기반 그룹 정보 조회
     * @param grpUid
     * @return
     * @throws Exception
     */
    // @Cacheable(value="plantGroupInfo", key="#grpUid")
    @Transactional(readOnly = true)
    public Group findPlantGroupByKey(Long grpUid) throws Exception
    {
        return plantDAO.selectPlantGroupsByKey(grpUid);
    }

    /**
     * 플랜트 그룹등록
     * @param data
     * @return
     */
    // @CacheEvict(value = "plantGroupInfo", allEntries = true)
    @Transactional
    public Group createGroups(@NotNull Group data)
    {
        if ( StringUtils.isBlank(data.getGrpName()) ) {
            throw new CInvalidArgumentException("그룹명은 필수항목입니다.");
        }

        try {
            int cnt = plantDAO.insertPlantGroups(data);
            if (cnt == 1) {
                return data;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * 플랜트 그룹 수정
     * @param group
     * @return
     * @throws Exception
     */
    // @CacheEvict(value = "plantGroupInfo", allEntries = true)
    @Transactional
    public Group modifyGroups(@NonNull Group group) throws Exception
    {
        if (group.getGrpUid() == null) {
            throw new CRequiredException();
        }
        int updated = plantDAO.updatePlantGroups(group);
        if (updated > 0) {
            return group;
        } else {
            return null;
        }
    }


    /**
     * 플랜트 그룹 삭제
     *  - 플랜트 삭제
     * @param groupId
     * @return
     */
    // @CacheEvict(value = "plantGroupInfo", allEntries = true)
    @Transactional
    public int removeGroups(@NotNull Long groupId)
    {
        try {
            int cnt = plantDAO.deletePlantGroups(groupId);
            if (cnt ==  1) {
                // 그룹 플랜트 삭제
                plantDAO.deleteAllPlantGroupItem(groupId);
            }
            return cnt;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Transactional(readOnly = true)
    public List<GroupPlant> findPlantGroupItem(Long grpUid) throws Exception
    {
        if (grpUid == null || grpUid.longValue() == 0) {
            throw new CRequiredException();
        }

        List<GroupPlant> resultSet = plantDAO.selectPlantGroupItem(grpUid);
        return resultSet;
    }

    @Transactional(readOnly = true)
    public List<Plant> findPlantGroupOuterItem() throws Exception
    {
        /**
        if (plantCd == null) {
            throw new CRequiredException();
        }*/
        return plantDAO.selectPlantGroupOtherItems();
    }

    /**
     * 사용자 그룹 멤버 등록
     * @param data
     * @return
     */
    @Transactional
    public GroupPlant createPlantGroupItem(@NotNull GroupPlant data)
    {
        if ( (data.getPlantCd() == null)) {
            throw new CInvalidArgumentException("플랜트 코드는 필수항목입니다.");
        } else if ( (data.getGrpUid() == null) || (data.getGrpUid() < 1)) {
            throw new CInvalidArgumentException("그룹 ID는 필수항목입니다.");
        }

        try {
            Plant resultSet = plantService.findPlantByKey(data.getPlantCd());
            data.setPlantNm(resultSet.getPlantNm());
            data.setCorpCd(resultSet.getCorpCd());
            data.setPlantCountry(resultSet.getPlantCountry());

            int cnt = plantDAO.insertPlantGroupItem(data);
            if (cnt == 1) {
                return data;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }


    @Transactional
    public int modifyGroupItem(@NonNull GroupPlant conn, Long reqUserUid) throws Exception
    {
        //try {
        //    if (conn.getGrpUid() == null) {
        //        throw new CRequiredException();
        //    }
        //    conn.setModUid(reqUserUid);
        //    int updated = plantDAO.updatePlantGroupItem(conn);
        //    if (updated == 1) {
        //        return conn;
        //    }
        //} catch (Exception e) {
        //    log.error(e.getMessage());
        //}
        //return null;

        if ( (conn.getGrpUid() == null || conn.getPlantCd() == null)) {
            throw new CInvalidArgumentException("필수항목입니다.");
        }

        try {
            conn.setModUid(reqUserUid);
            return plantDAO.updatePlantGroupItem(conn);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }


    /**
     * 사용자 그룹 개별 멤버 삭제
     * @param data
     * @return
     */
    @Transactional
    public int removePlantGroupItem(@NotNull GroupPlant data)
    {
        if ( (data.getPlantCd() == null)) {
            throw new CInvalidArgumentException("사용자 UID는 필수항목입니다.");
        }

        try {
            return plantDAO.deletePlantGroupItem(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }
}
