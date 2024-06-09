package com.guroomsoft.icms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Cross Origin Resource Sharing
 * JWT 를 사용할때 반드시 해주기 "CORS 정책"
 * 다른 출처의 자원을 사용할수 있게 허용해주는 정책
 * 현재는 disable 되어 적용되어 있지 않음
 */
@Slf4j
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); //내서버가 응답을 할때 json을 자바스크립트에서 처리할 수 있게 할지
        config.addAllowedOriginPattern("*"); //모든 아이피를 응답허용
        config.addAllowedHeader("*"); //모든 header 응답허용
        config.addAllowedMethod("*"); //모든 post,get,put 허용
        config.addExposedHeader("Content-Disposition"); // header 값 접근 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
