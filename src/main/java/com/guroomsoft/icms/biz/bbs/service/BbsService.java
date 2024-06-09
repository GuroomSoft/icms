package com.guroomsoft.icms.biz.bbs.service;

import com.guroomsoft.icms.auth.dao.UserDAO;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.biz.bbs.dao.BbsDAO;
import com.guroomsoft.icms.biz.bbs.dto.Notice;
import com.guroomsoft.icms.biz.bbs.dto.NoticeReq;
import com.guroomsoft.icms.common.dao.AttacheFileDAO;
import com.guroomsoft.icms.common.dto.AttachFileDtl;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CInvalidArgumentException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BbsService {
    public enum BBS_TYPE {
        IN_ANNOUNCE("IA"),      // 내부 공지사항
        PARTNER_ANNOUNCE("PA"),   // 협력사 공지사항
        IN_NOTICE("IN"),        // 내부 업무 게시판
        PARTNER_NOTICE("PN")      // 협력사 게시판
        ;

        String value;
        BBS_TYPE(String value) { this.value = value; }

        public String getValue() { return this.value; }
    }

    private final BbsDAO bbsDAO;

    private final AttacheFileDAO attacheFileDAO;

    private final UserDAO userDAO;

    /**
     * 게시판 목록 조회(관리자용)
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    public List<Notice> findNoticeForAdmin(NoticeReq cond) {
        try {
            List<Notice> items = bbsDAO.selectNoticeForAdmin(cond);
            for (Notice item : items)
            {
                // 첨부파일 목록
                if (StringUtils.isNotBlank(item.getAtchFileId())) {
                    List<AttachFileDtl> files = attacheFileDAO.selectFileDtls( item.getAtchFileId() );
                    item.setAttachFiles(files);
                }
            }
            return items;
        } catch (Exception e) {
            log.error("{} findBbs {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public int getNoticeCountForAdmin(NoticeReq cond) throws Exception
    {
        if ( (cond == null) || StringUtils.isBlank(cond.getBbsId()) ) {
            throw new CRequiredException();
        }

        try {
            return bbsDAO.selectNoticeCountForAdmin(cond);
        } catch (Exception e) {
            log.error("{} getNoticeCountForAdmin {}", this.getClass().getSimpleName(), e.getMessage());
            return -1;
        }
    }

    @Transactional(readOnly = true)
    public int getNoticeCount(NoticeReq cond) {
        try {
            return bbsDAO.selectNoticeCount(cond);
        } catch (Exception e) {
            log.error("{} getNoticeCount {}", this.getClass().getSimpleName(), e.getMessage());
            return -1;
        }
    }

    @Transactional(readOnly = true)
    public List<Notice> findNotice(NoticeReq cond) {
        try {
            List<Notice> items = bbsDAO.selectNotice(cond);
            for (Notice item : items)
            {
                // 첨부파일 목록
                if (StringUtils.isNotBlank(item.getAtchFileId())) {
                    List<AttachFileDtl> files = attacheFileDAO.selectFileDtls( item.getAtchFileId() );
                    item.setAttachFiles(files);
                }
            }
            return items;
        } catch (Exception e) {
            log.error("{} findBbs {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 대시보드용 공지목록
     * @return
     */
    @Transactional(readOnly = true)
    public Map<String, Object> findNoticeForDashboard()
    {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            NoticeReq cond = new NoticeReq();
            cond.setBbsId(BBS_TYPE.IN_ANNOUNCE.getValue());
            List<Notice> inBbs = bbsDAO.selectNoticeForDashboard(cond);
            resultMap.put("inBbs", inBbs);

            NoticeReq cond2 = new NoticeReq();
            cond2.setBbsId(BBS_TYPE.PARTNER_ANNOUNCE.getValue());
            List<Notice> outBbs = bbsDAO.selectNoticeForDashboard(cond2);
            resultMap.put("partnerBbs", outBbs);
            return resultMap;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> findNoticeForNotification(String bbsId)
    {
        if (StringUtils.isBlank(bbsId)) {
            throw new CInvalidArgumentException();
        }

        try {
            List<Map<String, Object>> resultMap = bbsDAO.getHeaderList(bbsId);
            return resultMap;
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }


    /**
     * 게시판 상세 조회 + 첨부파일 정보
     * @param bbsId
     * @param nttUid
     * @return
     */
    @Transactional(readOnly = true)
    public Notice findNoticeByKey(String bbsId, Long nttUid) {
        try {
            Notice item = bbsDAO.selectNoticeByKey(bbsId, nttUid);
            if ( (item != null) && StringUtils.isNotBlank(item.getAtchFileId()) ) {
                List<AttachFileDtl> files = attacheFileDAO.selectFileDtls(item.getAtchFileId());
                item.setAttachFiles(files);
            }
            return item;
        } catch (Exception e) {
            log.error("{} findBbsByKey {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 게시물 등록
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public Notice createNotice(Notice data) throws Exception
    {
        if (StringUtils.isBlank(data.getWriterId()))
        {
            User user = userDAO.selectUserByKey(Long.valueOf(data.getRegUid()));
            if (user != null) {
                data.setWriterId(user.getAccountId());
            }
        }

        // 디폴트 설정
        data = setNoticeDefault(data);
        // 유효성 검증
        if (!isValidNotice(data)) {
            throw new CRequiredException();
        }

        try {
            int inserted = bbsDAO.insertNotice(data);
            if (inserted == 0) {
                return null;
            }

            return data;
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    /**
     * 공지사항 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyNotice(Notice data) throws Exception
    {
        if ( (data == null) || StringUtils.isBlank(data.getBbsId())
                ||  (data.getNttUid() == null) || (data.getNttUid() == 0) )
        {
            throw new CRequiredException();
        }

        return bbsDAO.updateNotice(data);
    }

    @Transactional
    public void modifyNoticeReadCount(String bbsId, Long nttUid)
    {
        if (StringUtils.isBlank(bbsId) || nttUid == null || Long.valueOf(nttUid) == 0) {
            return ;
        }

        Notice data = new Notice();
        data.setBbsId(bbsId);
        data.setNttUid(nttUid);

        try {
            bbsDAO.updateNoticeReadCount(data);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    /**
     * 게시물 삭제
     *    - 첨부파일 삭제
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeNotice(Notice data) throws Exception
    {
        if ( (data == null) || StringUtils.isBlank(data.getBbsId()) || (data.getNttUid().longValue() == 0) )
        {
            throw new CInvalidArgumentException();
        }

        int deleted = bbsDAO.deleteNotice(data);
        if (deleted > 0) {
            if (StringUtils.isNotBlank(data.getAtchFileId()))
            {
                List<AttachFileDtl> details = attacheFileDAO.selectFileDtls(data.getAtchFileId());
                if ( (details != null) && !details.isEmpty() ) {
                    for (AttachFileDtl dtlFile : details)
                    {
                        int deletedFileCount = attacheFileDAO.deleteFileDtl(dtlFile);
                        if (deletedFileCount > 0) {
                            // 실제 파일 삭제처리
                            File realFile = new File(dtlFile.getFileStorePath() + File.separator + dtlFile.getStoreFileNm());
                            realFile.delete();
                        }
                    }
                }

                attacheFileDAO.deleteFile(data.getAtchFileId());

            }
        }

        return deleted;
    }

    public boolean isValidNotice(Notice data)
    {
        if (StringUtils.isBlank(data.getBbsId())
                || StringUtils.isBlank(data.getNttCategory())
                || StringUtils.isBlank(data.getNttSubject())
                || StringUtils.isBlank(data.getNttContent())
                || StringUtils.isBlank(data.getWriterId())
                ) return false;

        return true;
    }

    public Notice setNoticeDefault(Notice data) {
        data.setWriteDt(DateTimeUtil.currentDatetime());

        if (StringUtils.isBlank( data.getTopLockAt() )) {
            data.setTopLockAt(AppContant.CommonValue.NO.getValue());
        }

        data.setReadCount(0);
        if (StringUtils.isBlank(data.getAnswerAt())) {
            data.setAnswerAt(AppContant.CommonValue.NO.getValue());
            data.setAnswerLvl(0);
        }

        data.setSortOrder(0);
        return data;
    }


}
