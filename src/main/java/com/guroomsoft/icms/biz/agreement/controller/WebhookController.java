package com.guroomsoft.icms.biz.agreement.controller;

import com.guroomsoft.icms.biz.agreement.dto.WebhookResponse;
import com.guroomsoft.icms.biz.agreement.service.AgreementService;
import com.guroomsoft.icms.common.dto.CommonResult;
import com.guroomsoft.icms.common.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "합의서(전자계약) Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhook/eformsign")
public class WebhookController {
    private final ResponseService responseService;
    private final AgreementService agreementService;

    @Operation(summary = "전자계약문서 상태 업데이트 Webhook", description = "전자계약문서 상태 업데이트 Webhook")
    @RequestMapping(value = "/changeStatus", method = {RequestMethod.POST})
    public CommonResult changeEformsignDocStatus(
            @Parameter(description = "Webhook 응답", required = true) @RequestBody WebhookResponse param)
    {
        try {
            agreementService.notifyUpdateEformsignStatus(param);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return responseService.getSuccessResult();
    }

}
