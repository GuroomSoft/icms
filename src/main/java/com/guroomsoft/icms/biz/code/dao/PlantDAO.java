package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.biz.code.dto.Plant;
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
}
