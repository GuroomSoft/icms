package com.guroomsoft.icms.biz.material.dao;

import com.guroomsoft.icms.biz.material.dto.Material;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table : 원소재마스터 정보
 *  MT_MATERIAL
 *
 */
@Mapper
public interface MaterialDAO {
    /* MT_MATERIAL */
    List<Material> selectMaterial(Material cond) throws Exception;
    Material selectMaterialByKey(String materialCd, String materialNm) throws Exception;
    int insertMaterial(Material cond) throws Exception;
    int updateMaterial(Material cond) throws Exception;
    int saveMaterial(Material cond) throws Exception;
    int deleteMaterial(String materialCd, String materialNm) throws Exception;
}
