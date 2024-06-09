package com.guroomsoft.icms.common.service;

import com.guroomsoft.icms.common.dao.AttacheFileDAO;
import com.guroomsoft.icms.common.dto.AttachFile;
import com.guroomsoft.icms.common.dto.AttachFileDtl;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.util.AppContant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachFileService {
    private final AttacheFileDAO attacheFileDAO;

    /**
     * 첨부파일 마스터 등록
     * 사전에 attach file uuid 값이 지정되어서 등록되어야 한다.
     * @param data
     * @return
     */
    @Transactional
    public AttachFile createAttachFile(@NonNull AttachFile data)
    {
        if (StringUtils.isBlank(data.getAtchFileId())) {
            throw new CRequiredException();
        }

        try {
            // 이미 attached file id 가 존재하면 ignore 됨.
            attacheFileDAO.insertFile(data);

            List<AttachFileDtl> files = data.getFiles();
            if ( (files != null) && (files.size() > 0)) {
                for (AttachFileDtl file: files) {
                    file.setAtchFileId(data.getAtchFileId());
                    // todo 상대 경로 디폴트 값 수정 필요
                    file.setRelativePath(StringUtils.defaultString(file.getRelativePath(), "/corporation"));
                    file.setUseAt(AppContant.CommonValue.YES.getValue());
                    file.setRegUid(data.getRegUid());
                    attacheFileDAO.insertFileDtl(file);
                }
            }
            return data;

        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }

    /**
     * 첨부파일 마스터 등록
     * attach file uuid 값이 자동 등록되어 반환
     * @param data
     * @return
     */
    @Transactional
    public AttachFile createAttachFileWithKey(@NonNull AttachFile data)
    {
        try {
            int insCount = attacheFileDAO.insertFileWithKey(data);
            if (insCount == 1) {
                return data;
            }
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }

        return null;
    }

    /**
     * 첨부파일 조회
     * @param atchFileId
     * @return
     */
    public AttachFile findAttachFileByKey(String atchFileId)
    {
        if (StringUtils.isBlank(atchFileId)) {
            throw new CRequiredException();
        }
        AttachFile resultSet = new AttachFile();
        try {
            resultSet = attacheFileDAO.selectFileByKey(atchFileId);
            if (resultSet != null) {
                List<AttachFileDtl> fileDtls = findAttachFileDtls(resultSet.getAtchFileId());
                resultSet.setFiles(fileDtls);
            }
        } catch (Exception e) {
            log.error("findAttachFileByKey {}", e.getMessage());
        }
        return resultSet;
    }

    /**
     * 첨부파일 UUID 생성
     * @return
     */
    public String getNewAttachFileUUID()
    {
        return attacheFileDAO.selectAttachUUID();
    }

    /**
     * 첨부파일 마스터 정보 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyAttachFile(AttachFile data) throws Exception
    {
        if ( (data == null) || StringUtils.isBlank(data.getAtchFileId()) ) {
            throw new CRequiredException();
        }

        return attacheFileDAO.updateFile(data);
    }

    /**
     * 첨부파일 삭제
     * @param atchFileId
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeAttachFile(String atchFileId) throws Exception
    {
        int deleted = attacheFileDAO.deleteFile(atchFileId);
        if (deleted > 0) {
            List<AttachFileDtl> dtlList = findAttachFileDtls(atchFileId);
            if (dtlList != null) {
                for(AttachFileDtl file : dtlList) {
                    removeAttachFileDtl(file);
                }
            }
        }

        return deleted;
    }

    /**
     * 첨부파일 정보 삭제
     * @param param
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeAttachFileDtl(AttachFileDtl param) throws Exception
    {
        AttachFileDtl fileDtl = findAttachFileDtlByKey(param.getAtchFileId(), param.getFileSn());
        if (fileDtl == null) {
            return 0;
        }

        int deleted = attacheFileDAO.deleteFileDtl(param);
        if (deleted > 0) {
            // 실제 파일 삭제처리
            File realFile = new File(fileDtl.getFileStorePath() + File.separator + fileDtl.getStoreFileNm());
            realFile.delete();
        }
        return deleted;
    }

    /**
     * 첨부파일 삭제처리
     * @param atchFileId
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    public int removeAttachFile(String atchFileId, Long reqUserUid) throws Exception
    {
        AttachFile data = new AttachFile();
        data.setAtchFileId(atchFileId);
        data.setUseAt(AppContant.CommonValue.NO.getValue());
        data.setModUid(reqUserUid);
        int updated = modifyAttachFile(data);
        if (updated > 0) {
            // TODO 상세 항목 업데이트
        }

        return updated;
    }

    /**
     * 첨부파일 상세 목록
     * @param atchFileId
     * @return
     * @throws Exception
     */
    public List<AttachFileDtl> findAttachFileDtls(String atchFileId) throws Exception
    {
        if (StringUtils.isBlank(atchFileId)) {
            throw new CRequiredException();
        }

        List<AttachFileDtl> files =attacheFileDAO.selectFileDtls(atchFileId);
        return files;
    }

    /**
     * 첨부파일 수
     * @param atchFileId
     * @return
     * @throws Exception
     */
    public int getAttachFileCount(String atchFileId) throws Exception
    {
        return attacheFileDAO.selectFileDtlCount(atchFileId);
    }
    /**
     * 첨부파일 상세 단건 조회
     * @param atchFileId
     * @param fileSn
     * @return
     * @throws Exception
     */
    public AttachFileDtl findAttachFileDtlByKey(String atchFileId, Integer fileSn) throws Exception
    {
        if (StringUtils.isBlank(atchFileId)) {
            throw new CRequiredException();
        }

        if ( (fileSn == null) || (fileSn.intValue() == 0)) {
            throw new CRequiredException();
        }

        return attacheFileDAO.selectFileDtlByKey(atchFileId, fileSn);
    }

    /**
     * 첨부파일 상세 등록
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public AttachFileDtl createAttachFileDtl(AttachFileDtl data) throws Exception
    {
        // 정보 유효성 체크

        int inserted = attacheFileDAO.insertFileDtl(data);
        if (inserted > 0) {
            return data;
        } else {
            return null;
        }
    }

    /**
     * 첨부파일 상세 업데이트
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    public int modifyAttachFileDtl(AttachFileDtl data) throws Exception
    {
        if (data == null) {
            throw new CRequiredException();
        } else if (StringUtils.isBlank(data.getAtchFileId())) {
            throw new CRequiredException();
        } else if ( (data.getFileSn() == null) || (data.getFileSn().intValue() == 0) )  {
            throw new CRequiredException();
        }

        return attacheFileDAO.updateFileDtl(data);
    }

    /**
     * 마지막 등록된 파일 순번
     * @param atchFileId
     * @return
     * @throws Exception
     */
    public Long getLastFileSn(String atchFileId) throws Exception
    {
        if (StringUtils.isBlank(atchFileId)) {
            return Long.valueOf(-1);
        }

        return attacheFileDAO.selectLastFileSn(atchFileId);
    }



}
