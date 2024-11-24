package cn.lary.payment.config.alipay;

import cn.lary.common.exception.SystemException;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public AlipayConfig getAlipayConfig() {
        AlipayConfig config = new AlipayConfig();
        config.setAppId(appId);
        config.setProxyHost(gatewayHost);
        config.setSignType(signType);
        config.setAlipayPublicKey(alipayPublicKey);
        config.setFormat("json");
        config.setCharset("utf-8");
        config.setPrivateKey(merchantPrivateKey);
        //设置连接池中的最大可缓存的空闲连接数
        config.setMaxIdleConnections(5);
        //连接超时，单位：毫秒，默认3000
        config.setConnectTimeout(3000);
        //读取超时，单位：毫秒，默认15000
        config.setReadTimeout(15000);
        //空闲连接存活时间，单位：毫秒，默认10000L
        config.setKeepAliveDuration(10000L);
        return config;
    }

    @Bean
    public AlipayClient alipayClient(AlipayConfig config)  {
        DefaultAlipayClient client = null;
        try {
            client = new DefaultAlipayClient(config);
        } catch (AlipayApiException e) {
            throw new SystemException("alipay client init error", e.getMessage());
        }
        return client;
    }

}
