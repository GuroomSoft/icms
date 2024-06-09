package com.guroomsoft.icms.biz.code.controller;

import com.guroomsoft.icms.biz.code.dto.Code;
import com.guroomsoft.icms.biz.code.dto.CodeGroup;
import com.guroomsoft.icms.biz.code.service.CodeService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.dto.ListResult;
import com.guroomsoft.icms.common.exception.CAlreadyExistException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "공통코드 Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/code")
public class CodeController {
    private final ResponseService responseService;
    private final CodeService codeService;

    @Operation(summary = "공통코드 목록 조회", description = "공통코드 목록 조회")
    @RequestMapping(value = "/", method = {RequestMethod.POST})
    public ListResult<Code> findCodes(
            @Parameter(description = "조회조건", required = true) @RequestBody Code cond)
    {
        List<Code> resultSet = codeService.findCodes(cond);
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "공통코드 목록 조회", description = "공통코드 목록 조회")
    @RequestMapping(value = "/{cgId}", method = {RequestMethod.GET})
    public ListResult<Code> findCodes(
            @Parameter(description = "조회조건", required = true) @PathVariable String cgId)
    {
        Code cond = new Code();
        cond.setCgId(cgId);
        List<Code> resultSet = codeService.findCodes(cond);
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "공통코드 조회(단건)", description = "공통코드 조회(단건)")
    @RequestMapping(value = "/{cgId}/{cd}", method = {RequestMethod.GET})
    public DataResult<Code> findCodeById(
            @Parameter(description = "분류ID", required = true) @PathVariable String cgId,
            @Parameter(description = "공통코드", required = true) @PathVariable String cd)
    {
        Code data = codeService.findCodeByKey(cgId, cd);
        return responseService.getDataResult(data);
    }

    @Operation(summary = "공통코드 등록", description = "공통코드 등록처리")
    @RequestMapping(value = "/registration", method = {RequestMethod.POST})
    public DataResult<Code> registrationCode(
            @Parameter(description = "공통코드", required = true) @RequestBody Code data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setRegUid(Long.valueOf(reqUserUid));
            Code resultSet = codeService.createCode(data);
            return responseService.getDataResult(resultSet);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "공통코드 수정", description = "공통코드 수정처리")
    @RequestMapping(value = "/modify", method = {RequestMethod.POST})
    public CommonResult modifyCode(
            @Parameter(description = "공통코드", required = true) @RequestBody Code data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setModUid(Long.valueOf(reqUserUid));
            int updated = codeService.modifyCode(data);
            if (updated > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage() );
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공통코드 삭제", description = "공통코드 삭제처리")
    @RequestMapping(value = "/remove", method = {RequestMethod.POST})
    public CommonResult removeCodeG(
            @Parameter(description = "공통코드", required = true) @RequestBody Code data)
    {
        try {
            int deleted = codeService.removeCode(data.getCgId(), data.getCd());
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage() );
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }


    @Operation(summary = "공통코드 그룹 조회", description = "공통코드 그룹 조회")
    @RequestMapping(value = "/group", method = {RequestMethod.POST})
    public ListResult<CodeGroup> findCodeGroups(
            @Parameter(description = "조회조건", required = true) @RequestBody CodeGroup cond)
    {
        List<CodeGroup> resultSet = codeService.findCodeGroups(cond);
        return responseService.getListResult(resultSet);
    }

    @Operation(summary = "공통코드 분류 상세 조회", description = "공통코드 분류 상세 조회")
    @RequestMapping(value = "/group/{cgId}", method = {RequestMethod.GET})
    public DataResult<CodeGroup> findByKey(
            @Parameter(description = "코드분류 ID", required = true) @PathVariable(name="cgId") String cgId)
    {
        CodeGroup resultSet = codeService.findCodeGroupByKey(cgId);
        return responseService.getDataResult(resultSet);
    }

    @Operation(summary = "공통코드 분류 등록", description = "공통코드 분류 등록처리")
    @RequestMapping(value = "/group/registration", method = {RequestMethod.POST})
    public DataResult<CodeGroup> registrationCodeGroup(
            @Parameter(description = "코드분류", required = true) @RequestBody CodeGroup data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setRegUid(Long.valueOf(reqUserUid));
            CodeGroup resultSet = codeService.createCodeGroup(data);
            return responseService.getDataResult(resultSet);
        } catch (CAlreadyExistException e) {
            return responseService.getDataResult( CAlreadyExistException.getCode(), CAlreadyExistException.getCustomMessage(),null);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), CRequiredException.getCustomMessage(),null);
        } catch (Exception e) {
            return responseService.getDataResult(CUnknownException.getCode(), CUnknownException.getCustomMessage(),null);
        }
    }

    @Operation(summary = "공통코드 분류 수정", description = "공통코드 분류 수정처리")
    @RequestMapping(value = "/group/modify", method = {RequestMethod.POST})
    public CommonResult modifyCodeGroup(
            @Parameter(description = "공통코드 분류", required = true) @RequestBody CodeGroup data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            data.setModUid(Long.valueOf(reqUserUid));
            int updated = codeService.modifyCodeGroup(data);
            if (updated > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage() );
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공통코드 분류 삭제", description = "공통코드 분류 삭제처리")
    @RequestMapping(value = "/group/remove", method = {RequestMethod.POST})
    public CommonResult removeCodeGroup(
            @Parameter(description = "공통코드 분류", required = true) @RequestBody CodeGroup data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = codeService.removeCodeGroup(data.getCgId());
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage() );
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공통코드 분류 비활성화처리", description = "공통코드 분류 비활성 처리")
    @RequestMapping(value = "/group/inactive", method = {RequestMethod.POST})
    public CommonResult inactiveCodeGroup(
            @Parameter(description = "공통코드 분류", required = true) @RequestBody CodeGroup data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = codeService.disabledCodeGroup(data.getCgId(), reqUserUid);
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage() );
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

    @Operation(summary = "공통코드 분류 활성화처리", description = "공통코드 분류 활성 처리")
    @RequestMapping(value = "/group/active", method = {RequestMethod.POST})
    public CommonResult activeCodeGroup(
            @Parameter(description = "공통코드 분류", required = true) @RequestBody CodeGroup data,
            @Parameter(hidden = true) @RequestParam long reqUserUid)
    {
        try {
            int deleted = codeService.enabledCodeGroup(data.getCgId(), reqUserUid);
            if (deleted > 0) {
                return responseService.getSuccessResult();
            } else {
                return responseService.getFailResult();
            }
        } catch (CRequiredException e) {
            return responseService.getFailResult(CRequiredException.getCode(), CRequiredException.getCustomMessage() );
        } catch (Exception e) {
            return responseService.getFailResult(CUnknownException.getCode(), CUnknownException.getCustomMessage());
        }
    }

}
