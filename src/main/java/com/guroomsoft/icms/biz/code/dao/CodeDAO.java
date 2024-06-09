package com.guroomsoft.icms.biz.code.dao;

import com.guroomsoft.icms.biz.code.dto.Code;
import com.guroomsoft.icms.biz.code.dto.CodeGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table :
 *  ST_CODE_GROUP
 *  ST_CODE
 */
@Mapper
public interface CodeDAO {
    // ST_CODE_GROUP
    List<CodeGroup> selectCodeGroups(CodeGroup cond) throws Exception;
    CodeGroup selectCodeGroupByKey(String cgId) throws Exception;
    int insertCodeGroup(CodeGroup data) throws Exception;
    int updateCodeGroup(CodeGroup data) throws Exception;
    int deleteCodeGroup(String cgId) throws Exception;

    // ST_CODE
    List<Code> selectCodes(Code cond) throws Exception;
    Code selectCodeByKey(String cgId, String cd) throws Exception;
    int insertCode(Code data) throws Exception;
    int updateCode(Code data) throws Exception;
    int deleteCode(String cgId, String cd) throws Exception;

    String selectDefaultCode(String cgId) throws Exception;
}
