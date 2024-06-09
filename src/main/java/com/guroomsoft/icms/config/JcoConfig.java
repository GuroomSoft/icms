package com.guroomsoft.icms.config;

import com.guroomsoft.icms.sap.JcoInMemoryDestinationDataProvider;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRepository;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JcoConfig {
    public static String SAP_ABAP_AS = "ABAP_AS";

    @Value("${jco.client.client}")
    private String client;

    @Value("${jco.client.user}")
    private String sapUser;

    @Value("${jco.client.passwd}")
    private String sapPassword;

    @Value("${jco.client.lang}")
    private String clientLang;

    @Value("${jco.client.ashost}")
    private String applicationServerHost;

    @Value("${jco.client.sysnr}")
    private String sysnr;

    @Value("${jco.client.peak-limit}")
    private String peakLimit;

    @Value("${jco.client.pool-capacity}")
    private String poolCapacity;

    private final JcoInMemoryDestinationDataProvider jcoDestinationDataProvider;

    private JCoDestination jcoDestination;
    private JCoRepository jcoRepository;

    public JCoDestination registerProvider() throws Exception
    {
        log.debug("👉 JCO data provider를 등록합니다.");
        try {
            try{
                // register the provider with the JCo environment;
                // catch IllegalStateException if an instance is already registered

                Environment.registerDestinationDataProvider(jcoDestinationDataProvider);
            } catch (IllegalStateException providerAlreadyRegisteredException) {
                log.error("JCO data provider를 등록을 실패. 이미 등록된 Provider가 존재합니다.");
                throw new Error(providerAlreadyRegisteredException);
            }

            jcoDestinationDataProvider.changeProperties(SAP_ABAP_AS, getDestinationProperties());
            log.info("👉 연결정보를 구성합니다.");
            return JCoDestinationManager.getDestination(SAP_ABAP_AS);
        } catch (JCoException e) {
            log.error("👉 " + e.getMessage());
            throw new RuntimeException("Error configuring SAP JCo destination", e);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Fail to config sap jco destination", e);
        }
    }

    public void unregisteredProvider()
    {
        try {
            Environment.unregisterDestinationDataProvider(jcoDestinationDataProvider);
        } catch (Exception e) {
            throw new RuntimeException("Fail to unregister sap jco destination", e);
        }
    }

    @Bean
    public JCoDestination jcoDestination() throws Exception
    {
        if (jcoDestination == null) {
            jcoDestination = registerProvider();
        }
        return jcoDestination;
    }

    @Bean
    public JCoRepository jcoRepository() throws Exception
    {
        if (this.jcoDestination != null) {
            jcoRepository = jcoDestination.getRepository();
        } else {
            jcoDestination = jcoDestination();
            jcoRepository = jcoDestination.getRepository();
        }

        return jcoRepository;
    }

    private Properties getDestinationProperties()
    {
        log.debug("👉 JCO_ASHOST : {}", applicationServerHost);
        log.debug("👉 JCO_SYSNR : {}", sysnr);
        log.debug("👉 JCO_CLIENT : {}", client);
        log.debug("👉 JCO_USER : {}", sapUser);
        log.debug("👉 JCO_PASSWD : {}", sapPassword);
        log.debug("👉 JCO_LANG : {}", clientLang);
        log.debug("👉 JCO_PEAK_LIMIT : {}", peakLimit);
        log.debug("👉 JCO_POOL_CAPACITY : {}", poolCapacity);

        Properties connectProperties=new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, applicationServerHost);   // SAP Application Server IP address
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sysnr);            // Instance number
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, client);          // Client
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, sapUser);           // SAP user id
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, sapPassword);     // SAP user password
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, clientLang);
        connectProperties.setProperty(DestinationDataProvider.JCO_PEAK_LIMIT, peakLimit);           // 최대 컨텍션 풀 크기
        connectProperties.setProperty(DestinationDataProvider.JCO_POOL_CAPACITY, poolCapacity);     // 초기 컨텍션 풀 크기

        return connectProperties;
    }


}
