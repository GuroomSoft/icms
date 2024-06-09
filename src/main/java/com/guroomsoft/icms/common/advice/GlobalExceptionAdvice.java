package com.guroomsoft.icms.common.advice;

import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.exception.*;
import com.guroomsoft.icms.common.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "com.guroomsoft.icms")
public class GlobalExceptionAdvice {
    private final ResponseService responseService;

    @ExceptionHandler(CAlreadyExistException.class)
    protected CommonResult handleAlreadyExistException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CAlreadyExistException.getCode(),
                CAlreadyExistException.getCustomMessage() );
    }

    @ExceptionHandler(CAuthenticationException.class)
    protected CommonResult handleAuthenticationException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CAuthenticationException.getCode(),
                CAuthenticationException.getCustomMessage() );
    }

    @ExceptionHandler(CAuthorityException.class)
    protected CommonResult handleAuthorityException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CAuthorityException.getCode(),
                CAuthorityException.getCustomMessage() );
    }

    @ExceptionHandler(CBizProcessFailException.class)
    protected CommonResult handleBizProcessFailException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CBizProcessFailException.getCode(),
                CBizProcessFailException.getCustomMessage() );
    }

    @ExceptionHandler(CInvalidArgumentException.class)
    protected CommonResult handleInvalidArgumentException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CInvalidArgumentException.getCode(),
                CInvalidArgumentException.getCustomMessage() );
    }

    @ExceptionHandler(CInvalidAuthenticationException.class)
    protected CommonResult handleInvalidAuthenticationException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CInvalidAuthenticationException.getCode(),
                CInvalidAuthenticationException.getCustomMessage() );
    }

    @ExceptionHandler(CInvalidPasswordException.class)
    protected CommonResult handleInvalidPasswordException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CInvalidPasswordException.getCode(),
                CInvalidPasswordException.getCustomMessage() );
    }

    @ExceptionHandler(CTokenValidException.class)
    protected CommonResult handleTokenValidException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CTokenValidException.getCode(),
                CTokenValidException.getCustomMessage() );
    }

    @ExceptionHandler(CViolationCloseException.class)
    protected CommonResult handleViolationCloseException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CViolationCloseException.getCode(),
                CViolationCloseException.getCustomMessage() );
    }

    @ExceptionHandler(CUnknownException.class)
    protected CommonResult handleUnknownExceptionException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CUnknownException.getCode(),
                CUnknownException.getCustomMessage() );
    }

    @ExceptionHandler(CRequiredException.class)
    protected CommonResult handleRequiredException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CRequiredException.getCode(),
                CRequiredException.getCustomMessage() );
    }

    @ExceptionHandler(CRegistrationFailException.class)
    protected CommonResult handleRegistrationFailException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CRegistrationFailException.getCode(),
                CRegistrationFailException.getCustomMessage() );
    }

    @ExceptionHandler(CModifictionFailException.class)
    protected CommonResult handleModifictionFailException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CModifictionFailException.getCode(),
                CModifictionFailException.getCustomMessage() );
    }

    @ExceptionHandler(CRemoveFailException.class)
    protected CommonResult handleRemoveFailException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CRemoveFailException.getCode(),
                CRemoveFailException.getCustomMessage() );
    }

    @ExceptionHandler(CAlreadyCloseException.class)
    protected CommonResult handleAlreadyCloseException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CAlreadyCloseException.getCode(),
                CAlreadyCloseException.getCustomMessage() );
    }

    @ExceptionHandler(CDatabaseException.class)
    protected CommonResult handleDatabaseException(HttpServletRequest req, Exception e) {
        return responseService.getFailResult(
                CDatabaseException.getCode(),
                CDatabaseException.getCustomMessage() );
    }

}
