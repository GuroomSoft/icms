package com.guroomsoft.icms.mail.controller;

import com.guroomsoft.icms.mail.dto.EmailMessage;
import com.guroomsoft.icms.mail.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EmailController {
    private final EmailService emailService;

    @Operation(summary = "메일발송", description = "메일 발송")
    @RequestMapping(value = "/mail/send/plain", method = {RequestMethod.POST})
    public void sendMailPlainContent(@Parameter(description = "발송메일정보", required = true)  @RequestBody EmailMessage message) throws MessagingException {
        emailService.sendMailPlainContent(message);
    }

    @Operation(summary = "인증코드 메일발송", description = "인증코드 메일발송")
    @RequestMapping(value = "/mail/send/certNumber", method = {RequestMethod.POST})
    public void sendCertNumber(
            @Parameter(description = "수신자 메일주소", required = true)  @RequestParam String receiverMail,
            @Parameter(description = "로그인 계정ID", required = true)  @RequestParam String accountId) throws MessagingException
    {
        emailService.sendMailIndentVerification(receiverMail, accountId);
    }

}
