package com.guroomsoft.icms.common.interceptor;

import com.guroomsoft.icms.http.HttpReqRespUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import java.text.SimpleDateFormat;
import java.util.UUID;

@Slf4j
public class ApiLoggingInterceptor implements HandlerInterceptor {

    /**
     * API 호출에 대한 Logging
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
     * execution, for type and/or instance examination
     * @param ex any exception thrown on handler execution, if any; this does not
     * include exceptions that have been handled through an exception resolver
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String remoteAddr = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        String ip = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();

        // TODO API에 호출건에 대하여 로깅할 것

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
