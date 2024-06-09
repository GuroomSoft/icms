package com.guroomsoft.icms.biz.bbs.dao;

import com.guroomsoft.icms.biz.bbs.dto.Notice;
import com.guroomsoft.icms.biz.bbs.dto.NoticeReq;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Table : 게시판
 *  CT_BBS
 *
 */
@Mapper
public interface BbsDAO {
    /* 관리자용 */
    List<Notice> selectNoticeForAdmin(NoticeReq cond) throws Exception;
    List<Notice> selectNoticeForDashboard(NoticeReq cond) throws Exception;
    int selectNoticeCountForAdmin(NoticeReq cond) throws Exception;
    int insertNotice(Notice data) throws Exception;
    int updateNotice(Notice data) throws Exception;
    int updateNoticeReadCount(Notice data) throws Exception;
    int deleteNotice(Notice data) throws Exception;

    /* 일반사용자용 */
    List<Notice> selectNotice(NoticeReq cond) throws Exception;
    int selectNoticeCount(NoticeReq cond) throws Exception;

    Notice selectNoticeByKey(String bbsId, Long nttUid) throws Exception;
    List<Map<String, Object>> getHeaderList(String bbsId) throws Exception;
}
