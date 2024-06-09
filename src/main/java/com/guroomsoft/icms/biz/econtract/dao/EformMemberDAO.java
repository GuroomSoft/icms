package com.guroomsoft.icms.biz.econtract.dao;

import com.guroomsoft.icms.biz.econtract.dto.EformMember;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table :
 *  MT_EFORM_MEMBER
 */
@Mapper
public interface EformMemberDAO {
    // MT_EFORM_MEMBER
    int insertEformMember(EformMember data) throws Exception;
    int deleteEformMember(String efId) throws Exception;

    EformMember selectEformMember(String email) throws Exception;
    List<EformMember> selectEformMemberList() throws Exception;

}
