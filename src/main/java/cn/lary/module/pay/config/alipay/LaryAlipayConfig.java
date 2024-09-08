package cn.lary.module.pay.config.alipay;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LaryAlipayConfig {
    //@Value():将配置文件的值配置过来
    //appid
    @Value("${alipay.appId}")
    private String appId;
    //协议
    @Value("${alipay.protocol}")
    private String protocol;
    //网关
    @Value("${alipay.gatewayHost}")
    private String gatewayHost;
    //RSA2
    @Value("${alipay.signType}")
    private String signType;
    //私钥
    @Value("${alipay.merchantPrivateKey}")
    private String merchantPrivateKey;
    //支付宝公钥字符串即可
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;
    //可设置异步通知接收服务地址
    @Value("${alipay.notifyUrl}")
    private String notifyUrl;
}
