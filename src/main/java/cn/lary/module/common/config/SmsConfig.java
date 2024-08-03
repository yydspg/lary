package cn.lary.module.common.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsConfig {


    private String accessKeyId;


    private String accessKeySecret;


    private String signName;


    private String templateCode;

    // 可以添加其他配置属性

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public String getSignName() {
        return signName;
    }

    public String getTemplateCode() {
        return templateCode;
    }
    @PostConstruct
    public void init(){
        System.out.println("test");
        System.out.println(accessKeyId);
        System.out.println(accessKeySecret);
        System.out.println(signName);
        System.out.println(templateCode);
    }
}