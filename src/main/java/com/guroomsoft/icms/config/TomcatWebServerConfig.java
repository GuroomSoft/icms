package com.guroomsoft.icms.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.tomcat.TomcatReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TomcatWebServerConfig implements WebServerFactoryCustomizer<TomcatReactiveWebServerFactory> {
    @Override
    public void customize(TomcatReactiveWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "<>[\\]^`{|}"));
    }
}
