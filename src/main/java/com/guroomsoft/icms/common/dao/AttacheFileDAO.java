package com.guroomsoft.icms.common.dao;

import com.guroomsoft.icms.common.dto.AttachFile;
import com.guroomsoft.icms.common.dto.AttachFileDtl;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Table :
 *  CT_FILES
 *  시스템 환경설정 정보
 */
@Mapper
public interface AttacheFileDAO {
    /* 첨부파일 관련 */
    AttachFile selectFileByKey(String atchFileId) throws Exception;
    String selectAttachUUID();
    int insertFile(AttachFile data) throws Exception;
    int insertFileWithKey(AttachFile data) throws Exception;
    int updateFile(AttachFile data) throws Exception;
    int deleteFile(String atchFileId) throws Exception;

    /* 첨부파일 상세 관련 */
    List<AttachFileDtl> selectFileDtls(String atchFileId) throws Exception;
    int selectFileDtlCount(String atchFileId) throws Exception;
    AttachFileDtl selectFileDtlByKey(String atchFileId, Integer fileSn) throws Exception;
    int insertFileDtl(AttachFileDtl data) throws Exception;
    int updateFileDtl(AttachFileDtl data) throws Exception;
    int deleteFileDtl(AttachFileDtl data) throws Exception;
    Long selectLastFileSn(String atchFileId) throws Exception;
}
