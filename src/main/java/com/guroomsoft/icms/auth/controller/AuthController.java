package com.guroomsoft.icms.auth.controller;

import com.guroomsoft.icms.auth.dto.IdentifyVerification;
import com.guroomsoft.icms.auth.dto.JwtToken;
import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.provider.JwtProvider;
import com.guroomsoft.icms.auth.service.IdentifyVerificationService;
import com.guroomsoft.icms.auth.service.JwtService;
import com.guroomsoft.icms.auth.service.UserService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.dto.DataResult;
import com.guroomsoft.icms.common.exception.CAlreadyExistException;
import com.guroomsoft.icms.common.exception.CBizProcessFailException;
import com.guroomsoft.icms.common.exception.CRequiredException;
import com.guroomsoft.icms.common.exception.CUnknownException;
import com.guroomsoft.icms.common.service.ResponseService;
import com.guroomsoft.icms.mail.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Tag(name = "사용자 인증 Controller")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final ResponseService responseService;
    private final UserService userService;
    private final JwtService jwtService;
    private final IdentifyVerificationService identifyVerificationService;

    private final EmailService emailService;


    @Operation(summary = "로그인 사용자를 등록", description = "시스템에 접속할 로그인 사용자를 등록처리 후 반환")
    @RequestMapping(value = "/signup", method = {RequestMethod.POST})
    public DataResult<User> registrationUser(
            @Parameter(description = "사용자정보", required = true) @RequestBody User newUser)
    {
        // Login ID 중복여부 체크
        User oldUser = null;
        try {
            oldUser = userService.findByAccountId(newUser.getAccountId());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CBizProcessFailException();
        }

        if (oldUser != null) {
            throw new CAlreadyExistException("동일한 사용자 ID가 이미 존재합니다.");
        }

        try {
            User user = userService.createUser(newUser, Long.valueOf(1));
            return responseService.getDataResult(user);
        } catch (CRequiredException e) {
            return responseService.getDataResult( CRequiredException.getCode(), e.getMessage(),null);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CUnknownException();
        }
    }

    @Operation(summary = "인증코드발송", description = "인증코드 발송")
    @RequestMapping(value = "/publishAuthCode", method = {RequestMethod.POST})
    public CommonResult publishAuthencationCode(
            @Parameter(description = "수신자메일주소", required = true) @RequestParam String receiverMail,
            @Parameter(description = "계정ID", required = true) @RequestParam String accountId)
    {
        try {
            emailService.sendMailIndentVerification(receiverMail, accountId);
            return responseService.getSuccessResult();
        } catch (MessagingException e) {
            return responseService.getFailResult();
        }
    }

    @Operation(summary = "인증코드체크", description = "인증코드에 유효성 체크")
    @RequestMapping(value = "/checkAuthCode", method = {RequestMethod.POST})
    public CommonResult checkAuthencationCode(
            @Parameter(description = "인증코드정보", required = true) @RequestBody IdentifyVerification authCode)
    {
        if (identifyVerificationService.checkIdentifyVerification(authCode)) {
            return responseService.getSuccessResult();
        } else {
            return responseService.getFailResult();
        }
    }


    @RequestMapping(value = "/reissue", method = {RequestMethod.POST})
    @Operation(summary = "토큰 재발급", description = "엑세스 토큰이 만료되었을때 리프레시 토큰으로 요청하게 되면 엑세스,리프레시 토큰을 재발급해줍니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰이 재발급되었습니다"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리소스 접근",  content = @Content(schema = @Schema(implementation = CommonResult.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server error",  content = @Content(schema = @Schema(implementation = CommonResult.class)))
    })
    public DataResult<JwtToken> recreateToken(@RequestHeader(JwtProvider.REFRESH_HEADER_STRING) String refreshToken) {
        JwtToken jwtToken = jwtService.reissue(refreshToken);
        // UserTokenDTO userTokenDTO = new UserTokenDTO(jwtToken);
        return responseService.getDataResult(jwtToken);
    }


}
