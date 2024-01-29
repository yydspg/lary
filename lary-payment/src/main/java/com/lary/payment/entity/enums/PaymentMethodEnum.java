package com.lary.payment.entity.enums;

/**
 * @author paul 2024/1/29
 */

public enum PaymentMethodEnum {
    WECHAT("wechatPlugin","wechat"),
    ALIPAY("aliPayPlugin","alipay"),
    UNIONPAY("unionPayPlugin","unionPayCloudFlashPay"),
    WALLET("walletPlugin","balance");
    /*
    插件调用id,实现payment接口
     */
    private final String plugin;
    /*
    支付名称
     */
    private final String paymentName;

     PaymentMethodEnum(String plugin,String paymentName) {
        this.plugin = plugin;
        this.paymentName = paymentName;
    }

}
