package com.guroomsoft.icms.config;

import com.guroomsoft.icms.auth.interceptor.RefreshTokenAuthInterceptor;
import com.guroomsoft.icms.common.interceptor.ApiLoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Matcher;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${file.upload-root-dir}")
    private String rootFolderLocation;

    @Value("${file.preview-path-pattern}")
    private String previewPathPattern;

    @Value("${file.download-path-pattern}")
    private String downloadPathPattern;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Refresh token interceptor - check refresh token validation
        registry.addInterceptor(refreshTokenAuthInterceptor())
                .addPathPatterns("/reissue");

        // Api Logging interceptor
        String[] inArr = {"/api/**"};
        String[] exArr = {"/images/**", "/download/**", "/upload/**", "/preview/**"};

        registry.addInterceptor(apiLoggingInterceptor())
                .addPathPatterns(Arrays.asList(inArr))
                .excludePathPatterns(Arrays.asList(exArr));
    }

    @Bean
    RefreshTokenAuthInterceptor refreshTokenAuthInterceptor() {
        return new RefreshTokenAuthInterceptor();
    }

    @Bean
    ApiLoggingInterceptor apiLoggingInterceptor() {
        return new ApiLoggingInterceptor();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path relativePath = Paths.get(rootFolderLocation);
        String path = relativePath.toAbsolutePath().normalize().toString()
                .replaceAll(Matcher.quoteReplacement(File.separator), "/") + StringUtils.replace(File.separator, File.separator, "/");

        registry.addResourceHandler(previewPathPattern)
                .addResourceLocations("file:///" + path)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry.addResourceHandler(downloadPathPattern)
                .addResourceLocations("file:///" + path)
                .setCachePeriod(3600)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
