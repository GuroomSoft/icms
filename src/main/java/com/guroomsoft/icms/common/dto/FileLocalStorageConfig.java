package com.guroomsoft.icms.common.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileLocalStorageConfig {
    private String uploadRootDir;

}
