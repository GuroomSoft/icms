package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.auth.dto.Group;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.biz.code.dto.GroupPlant;
import com.guroomsoft.icms.biz.code.dto.Plant;
import jakarta.validation.constraints.NotNull;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table : 플랜트 정보
 *  MT_PLANT
 *
 */
@Mapper
public interface PlantDAO {
    /* MT_PLANT */
    List<Plant> selectPlant(Plant cond) throws Exception;

    Plant selectPlantByKey(String plantCd) throws Exception;
    int insertPlant(Plant cond) throws Exception;
    int updatePlant(Plant cond) throws Exception;
    int deletePlant(String corpCd, String plantCd) throws Exception;

    List<Group> selectPlantGroups(Group data) throws Exception;
    Group selectPlantGroupsByKey(Long grpUid) throws Exception;
    int insertPlantGroups(@NotNull Group data) throws Exception;
    int updatePlantGroups(@NotNull Group data) throws Exception;
    int deletePlantGroups(Long grpUid) throws Exception;

    List<GroupPlant> selectPlantGroupItem(Long grpUid) throws Exception;
    List<Plant> selectPlantGroupOtherItems() throws Exception;

    int insertPlantGroupItem(@NotNull GroupPlant data) throws Exception;
    int updatePlantGroupItem(@NotNull GroupPlant data) throws Exception;
    int deletePlantGroupItem(@NotNull GroupPlant data) throws Exception;
    int deleteAllPlantGroupItem(@NotNull Long grpId) throws Exception;
}
