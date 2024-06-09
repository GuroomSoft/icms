package com.guroomsoft.icms.biz.code.service;

import com.guroomsoft.icms.biz.code.dao.CodeDAO;
import com.guroomsoft.icms.biz.code.dto.Code;
import com.guroomsoft.icms.biz.code.dto.CodeGroup;
import com.guroomsoft.icms.common.exception.CAlreadyExistException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.util.AppContant;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final CodeDAO codeDAO;

    /**
     * 공통코드 그룹 목록 조회
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    @Cacheable("codeGroupInfo")
    public List<CodeGroup> findCodeGroups(CodeGroup cond) {
        try {
            return codeDAO.selectCodeGroups(cond);
        } catch (Exception e) {
            log.error("{} findCodeGroups {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 공통코드 그룹 조회
     * @param cgId
     * @return
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "codeGroupInfo", key = "#cgId")
    public CodeGroup findCodeGroupByKey(@NotNull String cgId) {
        if (StringUtils.isBlank(cgId)) {
            throw new CRequiredException();
        }

        try {
            return codeDAO.selectCodeGroupByKey(cgId);
        } catch (Exception e) {
            log.error("{} findCodeGroupByKey {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 공통코드 그룹 등록
     * @param data
     * @return
     */
    @Transactional
    @CacheEvict(value = "codeGroupInfo", allEntries = true)
    public CodeGroup createCodeGroup(@NotNull CodeGroup data) throws Exception {
        // check validation
        data = setDefaultCodeGroup(data);
        if ( !isValidCodeGroup(data) ) {
            throw new CRequiredException();
        }

        if (isAlreadyExistCodeGroup(data)) {
            throw new CAlreadyExistException();
        }
        int inserted = codeDAO.insertCodeGroup(data);
        if (inserted == 0) {
            return null;
        } else {
            return data;
        }
    }

    /**
     * 공통코드 분류 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "codeGroupInfo", allEntries = true)
    public int modifyCodeGroup(@NotNull CodeGroup data) throws Exception {
        if ( StringUtils.isBlank(data.getCgId()) ) {
            throw new CRequiredException();
        }

        return codeDAO.updateCodeGroup(data);
    }

    /**
     * 공통코드 분류 삭제
     * @param cgId
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "codeGroupInfo", allEntries = true)
    public int removeCodeGroup(String cgId) throws Exception {
        if ( StringUtils.isBlank(cgId) ) {
            throw new CRequiredException();
        }
        int deleted = codeDAO.deleteCodeGroup(cgId);
        if (deleted == 1) {
            // 상세 코드 목록 삭제
            removeCode(cgId);
        }
        return deleted;
    }

    /**
     * 공통코드 분류 비활성화 처리
     * @param cgId
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "codeGroupInfo", allEntries = true)
    public int disabledCodeGroup(String cgId, Long reqUserUid) throws Exception {
        if ( StringUtils.isBlank(cgId) ) {
            throw new CRequiredException();
        }
        CodeGroup data = new CodeGroup();
        data.setCgId(cgId);
        data.setUseAt(AppContant.CommonValue.NO.getValue());
        data.setModUid(reqUserUid);
        int updated = codeDAO.updateCodeGroup(data);
        if (updated == 1) {
            // 상세 코드 비활성화
            Code codeData = new Code();
            codeData.setCgId(cgId);
            codeData.setUseAt(AppContant.CommonValue.NO.getValue());
            codeData.setModUid(reqUserUid);

            codeDAO.updateCode(codeData);
        }

        return updated;
    }

    /**
     * 공통코드 분류 활성화 처리
     * @param cgId
     * @param reqUserUid
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "codeGroupInfo", allEntries = true)
    public int enabledCodeGroup(String cgId, Long reqUserUid) throws Exception {
        if ( StringUtils.isBlank(cgId) ) {
            throw new CRequiredException();
        }

        CodeGroup data = new CodeGroup();
        data.setCgId(cgId);
        data.setUseAt(AppContant.CommonValue.YES.getValue());
        data.setModUid(reqUserUid);
        int updated = codeDAO.updateCodeGroup(data);
        if (updated == 1) {
            // 상세 코드 비활성화
            Code codeData = new Code();
            codeData.setCgId(cgId);
            codeData.setUseAt(AppContant.CommonValue.YES.getValue());
            codeData.setModUid(reqUserUid);

            codeDAO.updateCode(codeData);
        }

        return updated;
    }

    /**
     * 공통코드 분류 데이터 유효성 체크
     * @param data
     * @return
     */
    public boolean isValidCodeGroup(CodeGroup data)
    {
        if (data == null) {
            return false;
        } else if (StringUtils.isBlank(data.getCgId())
                || StringUtils.isBlank(data.getCgNm()) )
        {
            return false;
        }

        return true;
    }

    /**
     * 코드 분류 중복검사
     * @param data
     * @return
     */
    public boolean isAlreadyExistCodeGroup(CodeGroup data)
    {
        CodeGroup oldValue = findCodeGroupByKey(data.getCgId());
        if (oldValue != null) {
            return true;
        }
        return false;
    }

    /**
     * 공통코드 분류 디폴트 값 설정
     * @param data
     * @return
     */
    public CodeGroup setDefaultCodeGroup(CodeGroup data)
    {
        if ( StringUtils.isBlank(data.getDisplayAt()) ) data.setDisplayAt(AppContant.CommonValue.YES.getValue());
        return data;
    }

    /**
     * 공통코드 목록 조회
     * @param cond
     * @return
     */
    @Transactional(readOnly = true)
    @Cacheable("codeInfo")
    public List<Code> findCodes(Code cond)
    {
        try {
            return codeDAO.selectCodes(cond);
        } catch (Exception e) {
            log.error("{} findCodes {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * 공통코드 조회(단건)
     * @param cgId
     * @return
     */
//    @Cacheable(value = "codeInfo", key="#cgId.concat(:).concat(#cd)")
    @Transactional(readOnly = true)
    public Code findCodeByKey(@NotNull String cgId, @NotNull String cd)
    {
        if (StringUtils.isBlank(cgId) || StringUtils.isBlank(cd)) {
            return new Code();
        }

        try {
            return codeDAO.selectCodeByKey(cgId, cd);
        } catch (Exception e) {
            log.error("{} findCodeByKey {}", this.getClass().getSimpleName(), e.getMessage());
            return null;
        }
    }

//    @Cacheable(value = "codeInfo", key="#cgId.concat(:).concat(#cd)")
    @Transactional(readOnly = true)
    public String getCodeNameByKey(String cgId, String cd)
    {
        if (StringUtils.isBlank(cgId) || StringUtils.isBlank(cd)) {
            return "";
        }
        try {
            Code result = codeDAO.selectCodeByKey(cgId, cd);
            if (result != null) {
                return StringUtils.defaultString(result.getCdNm());
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 공통코드 데이터 유효성 체크
     * @param data
     * @return
     */
    public boolean isValidCode(Code data)
    {
        // check Not Null
        if (data == null) {
            return false;
        } else if (StringUtils.isBlank(data.getCgId())
                || StringUtils.isBlank(data.getCd())
                || StringUtils.isBlank(data.getCdNm()) )
        {
            return false;
        }

        return true;
    }

    /**
     * 코드 중복검사
     * @param data
     * @return
     */
    public boolean isAlreadyExistCode(Code data)
    {
        Code oldValue = findCodeByKey(data.getCgId(), data.getCd());
        if (oldValue != null) {
            return true;
        }
        return false;
    }

    /**
     * 공통코드 디폴트 값 설정
     * @param data
     * @return
     */
    public Code setDefaultCode(Code data)
    {
        if (StringUtils.isBlank(data.getDefaultAt())) data.setDefaultAt( AppContant.CommonValue.NO.getValue() );
        if (StringUtils.isBlank(data.getDisplayAt())) data.setDisplayAt( AppContant.CommonValue.YES.getValue() );
        if ( (data.getDisplayOrd() == null) || (data.getDisplayOrd().intValue() == 0) ) {
            data.setDisplayOrd(9999);
        }

        return data;
    }

    /**
     * 공통코드 등록
     * @param data
     * @return
     */
    @Transactional
    @CacheEvict(value = "codeInfo", allEntries = true)
    public Code createCode(@NotNull Code data) throws Exception
    {
        // check validation
        data = setDefaultCode(data);
        if ( !isValidCode(data) ) {
            throw new CRequiredException();
        }

        // 중복 체크
        if (isAlreadyExistCode(data)) {
            throw new CAlreadyExistException();
        }

        int inserted = codeDAO.insertCode(data);
        if (inserted == 0) {
            return null;
        } else {
            return data;
        }
    }

    /**
     * 공통코드 수정
     * @param data
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "codeInfo", allEntries = true)
    public int modifyCode(@NotNull Code data) throws Exception
    {
        if ( StringUtils.isBlank(data.getCgId()) || StringUtils.isBlank(data.getCd())) {
            throw new CRequiredException();
        }

        return codeDAO.updateCode(data);
    }

    /**
     * 공통코드 삭제
     * @param cgId
     * @param cd
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "codeInfo", allEntries = true)
    public int removeCode(String cgId, String cd) throws Exception {
        if ( StringUtils.isBlank(cgId) && StringUtils.isBlank(cd)) {
            throw new CRequiredException();
        }
        int deleted = codeDAO.deleteCode(cgId, cd);
        return deleted;
    }

    /**
     * 공통코드 삭제
     * @param cgId
     * @return
     * @throws Exception
     */
    @Transactional
    @CacheEvict(value = "codeInfo", allEntries = true)
    public int removeCode(String cgId) throws Exception {
        if ( StringUtils.isBlank(cgId)) {
            throw new CRequiredException();
        }
        int deleted = codeDAO.deleteCode(cgId, null);
        return deleted;
    }

}
