package com.guroomsoft.icms.auth.dao;

import com.guroomsoft.icms.auth.dto.*;
import org.apache.ibatis.annotations.Mapper;

/**
 * Table :
 *  ST_USERS
 *  ST_GROUPS
 */
@Mapper
public interface IdentifyVerficationDAO {
    int insertIdentifyVerification(IdentifyVerification data) throws Exception;
    int updateIdentifyVerification(IdentifyVerification data) throws Exception;
    int deleteIdentifyVerification(final String accountId) throws Exception;
    IdentifyVerification selectIdentifyVerification(final IdentifyVerification param) throws Exception;
}
