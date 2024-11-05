package cn.lary.module.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lary.account")
public class AccountConfig {
    private String systemUid;
    private String fileHelperUid;
    private String systemGroupUid;
    private String systemGroupName;
    private String adminUid;
}
