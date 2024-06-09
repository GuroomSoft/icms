package com.guroomsoft.icms.biz.bbs.controller;

import com.guroomsoft.icms.biz.bbs.dto.Notice;
import com.guroomsoft.icms.biz.bbs.dto.NoticeReq;
import com.guroomsoft.icms.biz.bbs.service.BbsService;
import com.guroomsoft.icms.common.dto.*;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CInvalidArgumentException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.common.service.AttachFileService;
import com.guroomsoft.icms.common.service.FileLocalStorageService;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.BrowserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "공지사항/게시판 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bbs")
public class BbsController {
    private final static String BBS_TARGET_PATH = "bbs";

    private final ResponseService responseService;
    private final BbsService bbsService;
    private final FileLocalStorageService fileLocalStorageService;
    private final AttachFileService attachFileService;

    private final AppConfigService appConfigService;

    @Operation(summary = "공지사항 목록 조회", description = "공지사항 목록 조회 게시 기간이 적용된 목록을 조회")
    @RequestMapping(value = "/{bbsId}", method = {RequestMethod.GET})
    public ListResult<Notice> findNotice(
            @Parameter(description = "게시판ID", required = true) @PathVariable String bbsId,
            @Parameter(description = "분류") @RequestParam(required = false) String nttCategory,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "페이지번호", required = true) @RequestParam(defaultValue = "1") int pageNumber,
            @Parameter(description = "페이지당 행수", required = true) @RequestParam(defaultValue = "20") int rowCountPerPage)
    {
        NoticeReq cond = new NoticeReq();
        cond.setBbsId(bbsId);
        if (StringUtils.isNotBlank(nttCategory)) cond.setNttCategory(nttCategory);
        if (StringUtils.isNotBlank(searchWord)) cond.setSearchWord(searchWord);

        List<Notice> resultSet = null;
        try {
            // 디폴트 페이지 당 조회 건수
            if (rowCountPerPage == 0) {
                rowCountPerPage = appConfigService.getIntValue(AppConfigService.ROWCOUNT_PER_PAGE);
            }

            cond.setRowCountPerPage(rowCountPerPage);
            if (cond.getRowCountPerPage() > 0) {
                cond.setTotalCount( bbsService.getNoticeCount(cond) );
                int rc = cond.getRowCountPerPage();
                int share = cond.getTotalCount() / rc;
                int remainder = cond.getTotalCount() % rc;

                if (remainder > 0) share++;

                cond.setTotalPageCount(share);
            }

            cond.setPageNumber(pageNumber);

            // 조회 요청 페이지가 전체 페이지수 보다 크면 빈 목록 반환
            if (cond.getTotalPageCount() < cond.getPageNumber()) {
                return responseService.getListPageResult(null, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
            }

            cond.setStartOffset((cond.getPageNumber()-1) * rowCountPerPage);
            resultSet = bbsService.findNotice(cond);
            return responseService.getListPageResult(resultSet, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }

    @Operation(summary = "공지사항 관리 목록 조회", description = "공지사항 관리목록 조회 게시기간이 적용안됨")
    @RequestMapping(value = "/manage/{bbsId}", method = {RequestMethod.GET})
    public ListResult<Notice> findNoticeForAdmin(
            @Parameter(description = "게시판ID", required = true) @PathVariable String bbsId,
            @Parameter(description = "분류") @RequestParam(required = false) String nttCategory,
            @Parameter(description = "검색어") @RequestParam(required = false) String searchWord,
            @Parameter(description = "등록일(From)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "등록일(To)") @RequestParam(required = false) String toDate,
            @Parameter(description = "페이지번호", required = true) @RequestParam(defaultValue = "1") int pageNumber,
            @Parameter(description = "페이지당 행수", required = true) @RequestParam(defaultValue = "20") int rowCountPerPage)
    {
        NoticeReq cond = new NoticeReq();
        cond.setBbsId(bbsId);
        if (StringUtils.isNotBlank(nttCategory)) cond.setNttCategory(nttCategory);
        if (StringUtils.isNotBlank(searchWord)) cond.setSearchWord(searchWord);
        if (StringUtils.isNotBlank(fromDate)) cond.setWriteDt(fromDate);
        if (StringUtils.isNotBlank(toDate)) cond.setWriteDt(toDate);

        List<Notice> resultSet = null;
        try {
            // 디폴트 페이지 당 조회 건수
            if (rowCountPerPage == 0) {
                rowCountPerPage = appConfigService.getIntValue(AppConfigService.ROWCOUNT_PER_PAGE);
            }

            cond.setRowCountPerPage(rowCountPerPage);
            if (cond.getRowCountPerPage() > 0) {
                cond.setTotalCount( bbsService.getNoticeCountForAdmin(cond) );
                int rc = cond.getRowCountPerPage();
                int share = cond.getTotalCount() / rc;
                int remainder = cond.getTotalCount() % rc;

                if (remainder > 0) share++;

                cond.setTotalPageCount(share);
            }

            cond.setPageNumber(pageNumber);

            // 조회 요청 페이지가 전체 페이지수 보다 크면 빈 목록 반환
            if (cond.getTotalPageCount() < cond.getPageNumber()) {
                return responseService.getListPageResult(null, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
            }

            cond.setStartOffset((cond.getPageNumber()-1) * rowCountPerPage);
            resultSet = bbsService.findNoticeForAdmin(cond);
            return responseService.getListPageResult(resultSet, cond.getTotalCount(), cond.getTotalPageCount(), cond.getPageNumber(), cond.getRowCountPerPage());
        } catch (Exception e) {
            throw new CBizProcessFailException();
        }
    }

    @Operation(summary = "게시물 상세조회", description = "게시물 상세조회")
    @RequestMapping(value = "/{bbsId}/{nttUid}", method = {RequestMethod.GET})
    public DataResult<Notice> findNoticeDetail(
            @Parameter(description = "게시판 ID", required = true) @PathVariable String bbsId,
            @Parameter(description = "게시물 UID", required = true) @PathVariable long nttUid)
    {
        Notice resultSet = bbsService.findNoticeByKey(bbsId, Long.valueOf(nttUid));
        bbsService.modifyNoticeReadCount(bbsId, Long.valueOf(nttUid));
        return responseService.getDataResult(resultSet);
    }

    @Operation(summary = "공지사항 삭제", description = "공지사항 삭제")
    @RequestMapping(value = "/manage/remove/{bbsId}", method = {RequestMethod.POST})
    public CommonResult removeNotice(
            @Parameter(description = "게시판 ID", required = true) @PathVariable String bbsId,
            @Parameter(description = "공지사항", required = true) @RequestBody Notice data)
    {
        try {
            if (StringUtils.isBlank(data.getBbsId())) data.setBbsId(bbsId);

            int deleted = bbsService.removeNotice(data);
            if (deleted > 0) {
                return responseService.getSuccessResult();
            }
            return responseService.getFailResult();
        } catch (CInvalidArgumentException e) {
            return responseService.getFailResult(CInvalidArgumentException.getCode(), CInvalidArgumentException.getCustomMessage());
        } catch (CBizProcessFailException e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "게시물 등록", description = "게시물 등록처리")
    @RequestMapping(value = "/manage/registration",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            method = {RequestMethod.POST})
    public DataResult<Notice> registrationNotice(
            @Parameter(description = "게시물", required = true) @RequestPart Notice data,
            @Parameter(description = "첨부파일") @RequestPart(required = false) MultipartFile[] attachedFiles,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setRegUid(reqUserUid);
            Notice resultSet = null;
            if ( (attachedFiles == null) || (attachedFiles.length == 0) ) {
                resultSet = bbsService.createNotice(data);
                return responseService.getDataResult(resultSet);
            }

            // 첨부파일이 있는 경우
            // new attached file uuid 생성
            String attachFileId = attachFileService.getNewAttachFileUUID();
            data.setAtchFileId(attachFileId);

            // 첨부파일 등록
            List<AttachFileDtl> files = fileLocalStorageService.uploadFiles(attachedFiles, BBS_TARGET_PATH);
            if (files != null)
            {
                AttachFile attachFile = new AttachFile();
                attachFile.setAtchFileId(attachFileId);
                attachFile.setUseAt(AppContant.CommonValue.YES.getValue());
                attachFile.setRegUid(reqUserUid);

                for (AttachFileDtl file : files) {
                    file.setAtchFileId(attachFileId);
                    file.setRegUid(reqUserUid);
                }
                attachFile.setFiles(files);
                attachFileService.createAttachFile(attachFile);
            }

            resultSet = bbsService.createNotice(data);
            return responseService.getDataResult(resultSet);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Operation(summary = "게시물 수정", description = "첨부파일을 포함한 게시물 수정처리")
    @RequestMapping(value = "/manage/modify",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            method = {RequestMethod.POST})
    public DataResult<Notice> modifyNotice(
            @Parameter(description = "게시물", required = true) @RequestPart Notice data,
            @Parameter(description = "첨부파일") @RequestPart(required = false) MultipartFile[] attachedFiles,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setModUid(reqUserUid);
            int updated = 0;
            if ( (attachedFiles == null) || (attachedFiles.length == 0) ) {
                updated = bbsService.modifyNotice(data);
                return responseService.getDataResult(data);
            }

            // 첨부파일이 있는 경우
            String attachFileId = null;
            if ( StringUtils.isBlank(data.getAtchFileId()) ) {
                // new attached file uuid 생성
                attachFileId = attachFileService.getNewAttachFileUUID();
            }

            // 첨부파일 등록
            List<AttachFileDtl> files = fileLocalStorageService.uploadFiles(attachedFiles, BBS_TARGET_PATH);
            if (files != null) {
                AttachFile attachFile = new AttachFile();
                attachFile.setAtchFileId(StringUtils.defaultString(data.getAtchFileId(), attachFileId));
                attachFile.setUseAt(AppContant.CommonValue.YES.getValue());
                attachFile.setRegUid(reqUserUid);

                for (AttachFileDtl file : files) {
                    file.setAtchFileId(attachFileId);
                    file.setRegUid(reqUserUid);
                }
                attachFile.setFiles(files);
                attachFileService.createAttachFile(attachFile);
            }

            if ( StringUtils.isBlank(data.getAtchFileId()) && StringUtils.isNotBlank(attachFileId)) {
                data.setAtchFileId(attachFileId);
            }

            updated = bbsService.modifyNotice(data);
            Notice resultSet = null;
            if (updated > 0) {
                resultSet = bbsService.findNoticeByKey(data.getBbsId(), data.getNttUid());
            }
            return responseService.getDataResult(resultSet);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }
    }

    @Operation(summary = "첨부파일 삭제", description = "첨부파일 삭제")
    @RequestMapping(value = "/manage/attachedFile/remove/{bbsId}/{nttId}", method = {RequestMethod.POST})
    public CommonResult removeNoticeAttachedFile(
            @Parameter(description = "게시판ID", required = true) @PathVariable String bbsId,
            @Parameter(description = "게시물ID", required = true) @PathVariable long nttId,
            @Parameter(description = "첨부파일정보", required = true) @RequestBody AttachFileDtl fileInfo,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        Notice data = bbsService.findNoticeByKey(bbsId, Long.valueOf(nttId));
        if (StringUtils.isBlank(fileInfo.getAtchFileId())
                || fileInfo.getFileSn() == null || fileInfo.getFileSn().intValue() == 0)
        {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage());
        }

        try {
            int deleted = attachFileService.removeAttachFileDtl(fileInfo);
            if (deleted > 0) {
                // todo 첨부파일이 더 이상 존재하지 않는 경우 이미 생성된 ID 값은 어떻게 처리할지 고민
                return responseService.getSuccessResult();
            }

            return responseService.getFailResult();
        } catch (Exception e) {
            return responseService.getFailResult(CBizProcessFailException.getCode(), CBizProcessFailException.getCustomMessage());
        }

    }

    @Operation(summary = "첨부파일 다운로드", description = "첨부파일 다운로드 처리")
    @RequestMapping(value = "/download/attachedFile/{atchFileId}/{fileSn}", method = {RequestMethod.GET})
    public void downloadAttachedFiles(
            @Parameter(description = "첨부파일ID", required = true) @PathVariable String atchFileId,
            @Parameter(description = "첨부파일순번", required = true) @PathVariable int fileSn,
            @Parameter(hidden = true) HttpServletRequest request,
            @Parameter(hidden = true) HttpServletResponse response) throws Exception
    {
        AttachFileDtl attachedFileInfo = attachFileService.findAttachFileDtlByKey(atchFileId, Integer.valueOf(fileSn));

        File downloadFile = new File(attachedFileInfo.getFileStorePath(), attachedFileInfo.getStoreFileNm());
        long fSize = downloadFile.length();

        if (fSize < 1) {
            return;
        }

        Path source = Paths.get(attachedFileInfo.getFileStorePath());
        String mimeType = StringUtils.defaultString(Files.probeContentType(source), "application/octet-stream");
        String userAgent = request.getHeader("User-Agent");
        String contentDisposition = BrowserUtil.getDisposition(attachedFileInfo.getOriginalFileNm(), userAgent, "UTF-8");

        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", contentDisposition);
        response.setContentLengthLong(fSize);

        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(downloadFile));
            out = new BufferedOutputStream(response.getOutputStream());

            FileCopyUtils.copy(in, out);
            out.flush();
        } catch (IOException e) {
            log.error("공지사항 첨부파일 다운로드 중 에러");
            log.error(e.getMessage());
        } finally {
            in.close();
            out.close();
        }
    }


    @Operation(summary = "최근 공지사항 목록(5건)", description = "최근 공지사항 목록(5건) 대시보드용")
    @RequestMapping(value = "/getLastList", method = {RequestMethod.GET})
    public DataResult<Map<String, Object>> getLastList()
    {
        Map<String, Object> resultMap = bbsService.findNoticeForDashboard();
        return responseService.getDataResult(resultMap);
    }

    @Operation(summary = "최근 공지사항 목록(5건)", description = "최근 공지사항 목록(5건) 대시보드용")
    @RequestMapping(value = "/getHeaderForNotice/{bbsId}", method = {RequestMethod.GET})
    public ListResult<Map<String, Object>> getNoticeHeader(
            @Parameter(description = "게시판 ID", required = true) @PathVariable String bbsId)
    {
        List<Map<String, Object>> resultMap = bbsService.findNoticeForNotification(bbsId);
        return responseService.getListResult(resultMap);
    }



}
