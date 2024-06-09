package com.guroomsoft.icms.mail.service;

import com.guroomsoft.icms.auth.dto.IdentifyVerification;
import com.guroomsoft.icms.auth.service.IdentifyVerificationService;
import com.guroomsoft.icms.common.service.AppConfigService;
import com.guroomsoft.icms.mail.dto.EmailMessage;
import com.guroomsoft.icms.util.AppContant;
import com.guroomsoft.icms.util.DateTimeUtil;
import com.guroomsoft.icms.util.GenerateCertNumber;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final IdentifyVerificationService identifyVerificationService;
    private final AppConfigService appConfigService;

    /**
     * 단순 텍스트 형식의 메일전송
     * @param mail
     * @throws MessagingException
     */
    public void sendMailPlainContent(EmailMessage mail) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");   // Mime 메시지, 파일전송여부, 인코딩
            messageHelper.setTo(mail.getReceiver());        // 수신자
            messageHelper.setSubject(mail.getTitle());      // 제목
            messageHelper.setText(mail.getContent(), false); // 본문, HTML 여부
            mailSender.send(mimeMessage);
            log.info("👉 Send mail success");
        } catch (MailException e) {
            log.error("👉 " + e.getMessage());
        }
    }

    // 본인 인증 메일 전송
    @Async
    public void sendMailIndentVerification(final String receiverMail, final String accountId) throws MessagingException {
        IdentifyVerification iv = createAuthCodeByMail(receiverMail, accountId);
        if ( (iv == null) || StringUtils.isBlank(iv.getCertCode())) {
            log.error("👉 Fail to create authentication code");
            return ;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("userMail", receiverMail);
        params.put("accountId", accountId);
        params.put("certCode", iv.getCertCode());

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");   // Mime 메시지, 파일전송여부, 인코딩
            EmailMessage mail = EmailMessage.builder()
                    .receiver(receiverMail)
                    .title("[회원가입] 시스템 회원 가입 인증번호")
                    .content(configTemplate("IdentifyVerificationTemplate", params))
                    .build();

            messageHelper.setTo(mail.getReceiver());                 // 수신자
            messageHelper.setSubject(mail.getTitle());               // 제목
            messageHelper.setText(mail.getContent(),  true);    // 본문, HTML 여부
            mailSender.send(mimeMessage);
            log.info("👉 Send mail success");
        } catch (MailException e) {
            log.error("👉 " + e.getMessage());
        }
    }

    /*
     * 인증 코드 정보 등록
     */
    @Transactional
    public IdentifyVerification createAuthCodeByMail(String mail, String accountId) {
        IdentifyVerification codeInfo = new IdentifyVerification();
        codeInfo.setAccountId(accountId);
        codeInfo.setUserEmail(mail);
        codeInfo.setCertCode(createCertCode());
        codeInfo.setCheckAt(AppContant.CommonValue.YES.getValue());
        try {
            int minutes = appConfigService.getIntValue(AppConfigService.AUTH_DURATION);
            codeInfo.setExpiredDt(DateTimeUtil.createExpiredDatetime(minutes));
        } catch (Exception e) {
            log.error("👉 인증코드 정보 생성 실패");
            return null;
        }

        return identifyVerificationService.createIdentifyVerification(codeInfo);
    }

    // 메일을 전송할 템플릿 설정
    public String configTemplate(final String templateName, final HashMap<String, Object> params) {
        Context context = new Context();
        context.setVariables(params);
        return templateEngine.process(templateName, context);
    }

    // 임의의 인증코드 생성
    private String createCertCode() {
        GenerateCertNumber ge = new GenerateCertNumber();
        ge.setCertNumLength(5);
        return ge.excuteGenerate();
    }



}
