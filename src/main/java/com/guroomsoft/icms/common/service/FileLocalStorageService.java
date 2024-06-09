package com.guroomsoft.icms.common.service;

import com.guroomsoft.icms.common.dto.AttachFileDtl;
import com.guroomsoft.icms.common.dto.FileLocalStorageConfig;
import com.guroomsoft.icms.common.exception.CFileStorageException;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 공통서비스
 * 파일 업로드 및 다운로드
 */
@Slf4j
@Service
public class FileLocalStorageService {
    private final Path fileRootDir;

    public FileLocalStorageService(FileLocalStorageConfig fileLocalStorageConfig)
    {
        // Create upload root directory
        this.fileRootDir = Paths.get(fileLocalStorageConfig.getUploadRootDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileRootDir);
        } catch (IOException e) {
            throw new CFileStorageException();
        }
    }

    /**
     * 디렉토리 생성
     * @param subDir
     * @return
     */
    public String checkCreateDirectory(String subDir)
    {
        String storePathString = this.fileRootDir.toString() + File.separator + subDir;
        File targetDir = new File(WebUtil.filePathBlackList(storePathString));
        if (targetDir.exists()) {
            return storePathString;
        } else {
            if (!targetDir.exists() || targetDir.isFile()) {
                if (targetDir.mkdirs()) {
                    log.debug("👉 {} createSubDirectory success", this.getClass().getSimpleName());
                    return storePathString;
                } else {
                    log.debug("👉 {} createSubDirectory fail", this.getClass().getSimpleName());
                    return null;
                }
            } else {
                log.debug("👉 {} target is not directory", targetDir.toString());
                return null;
            }
        }
    }

    /**
     * 파일 업로드
     * @param files         업로드 파일 목록
     * @param targetPath    파일 저장 Sub folder name
     * @return
     *        저장 파일 목록
     * @throws Exception
     */
    public List<AttachFileDtl> uploadFiles(MultipartFile[] files, String targetPath) throws Exception
    {
        List<AttachFileDtl> attachFileDtlList = new ArrayList<>();
        String fileLocation = checkCreateDirectory(targetPath);
        if (StringUtils.isBlank(fileLocation)) {
            throw new CFileStorageException();
        }
        int fileSeq = 1;

        for(MultipartFile file: files)
        {
            AttachFileDtl fileInfo = new AttachFileDtl();
            fileInfo.setFileStorePath(fileLocation);
            fileInfo.setRelativePath("/" + targetPath );
            fileInfo.setUseAt(AppContant.CommonValue.YES.getValue());

            String originFileName = file.getOriginalFilename();
            if (StringUtils.isBlank(originFileName)) {
                continue;
            }

            int index = originFileName.lastIndexOf(".");

            if (index == -1) {
                continue;
            }

            fileInfo.setOriginalFileNm(originFileName);

            // 파일 확장자
            fileInfo.setFileExtension(originFileName.substring(index + 1));
            // 저장 파일명 - 파일 형식
            // File prefix + timestampe + file 순번 + 파일 확장자
            fileInfo.setStoreFileNm(getTimestamp() + String.valueOf(fileSeq++) + "." + fileInfo.getFileExtension());
            fileInfo.setFileSn(fileSeq);
            // 파일 크기 (Byte 단위)
            fileInfo.setFileSize(Long.valueOf(file.getSize()));
            if (fileInfo.getFileSize().longValue() == 0) {
                continue;
            }

            // 파일 저장
            String fileFullName = fileLocation + File.separator + fileInfo.getStoreFileNm();
            file.transferTo(new File(WebUtil.filePathBlackList(fileFullName)));

            attachFileDtlList.add(fileInfo);
        }

        return attachFileDtlList;
    }

    /**
     * 파일 업로드
     * @param file          업로드 파일
     * @param targetPath    파일 저장 Sub folder name
     * @return
     *        저장 파일 정보
     * @throws Exception
     */
    public AttachFileDtl uploadFiles(MultipartFile file, String targetPath) throws Exception
    {
        String fileLocation = checkCreateDirectory(targetPath);
        if (StringUtils.isBlank(fileLocation)) {
            throw new CFileStorageException();
        }

        AttachFileDtl fileInfo = new AttachFileDtl();
        fileInfo.setFileStorePath(fileLocation);

        String originFileName = file.getOriginalFilename();
        if (StringUtils.isBlank(originFileName)) {
            return null;
        }

        int index = originFileName.lastIndexOf(".");

        if (index == -1) {
            return null;
        }

        fileInfo.setOriginalFileNm(originFileName);

        // 파일 확장자
        fileInfo.setFileExtension(originFileName.substring(index + 1));
        // 저장 파일명 - 파일 형식
        // File prefix + timestampe + file 순번 + 파일 확장자
        fileInfo.setStoreFileNm(getTimestamp() + "." + fileInfo.getFileExtension());
        fileInfo.setFileSn(1);
        // 파일 크기 (Byte 단위)
        fileInfo.setFileSize(Long.valueOf(file.getSize()));
        if (fileInfo.getFileSize().longValue() == 0) {
            return null;
        }

        // 파일 저장
        String fileFullName = fileLocation + File.separator + fileInfo.getStoreFileNm();
        try {
            file.transferTo(new File(WebUtil.filePathBlackList(fileFullName)));
            return fileInfo;
        } catch (IOException e) {
            log.debug("👉 Fail to upload file");
        }
        return null;
    }

    public boolean isImageFile(MultipartFile file)
    {
        if (file == null) return false;

        String extName = org.springframework.util.StringUtils.getFilenameExtension(file.getOriginalFilename()).toLowerCase();

        if (StringUtils.isBlank(extName)) return false;

        if (extName.equalsIgnoreCase("jpeg")
                || extName.equalsIgnoreCase("jpg")
                || extName.equalsIgnoreCase("png")
                || extName.equalsIgnoreCase("gif") ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 파일 삭제
     * @param fullPathFileName
     * @throws Exception
     */
    public void deleteFile(String fullPathFileName) throws Exception
    {
        File file = new File(fullPathFileName);
        file.delete();
    }
    public String getTimestamp()
    {
        SimpleDateFormat sdfCurrent = new SimpleDateFormat("yyyyMMddhhmmssSSS", Locale.KOREA);
        Timestamp ts = new Timestamp(System.currentTimeMillis());

        return sdfCurrent.format(ts.getTime());
    }

    /**
     * 파일 다운로드
     * @param fullPathFile
     * @return
     * @throws Exception
     */
    public byte[] download(String fullPathFile) throws Exception
    {
        byte[] data = null;

        File targetFile = new File(WebUtil.filePathBlackList(fullPathFile));

        if (!targetFile.exists() || !targetFile.isFile()) {
            throw new FileNotFoundException(fullPathFile);
        }

        try {
            Path path = Paths.get(fullPathFile).toAbsolutePath().normalize();
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new IOException("IO Error Message=" + e.getMessage());
        }
        return data;
    }
}
