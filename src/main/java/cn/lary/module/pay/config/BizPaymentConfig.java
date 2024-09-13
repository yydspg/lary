package cn.lary.module.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lary.biz.pay")
public class BizPaymentConfig {

    private String rechargePrefix;
    private String giftBuyPrefix;
}
