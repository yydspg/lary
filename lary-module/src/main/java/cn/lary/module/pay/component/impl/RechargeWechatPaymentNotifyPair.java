package cn.lary.module.pay.component.impl;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

@Data
public class RechargeWechatPaymentNotifyPair extends PaymentNotifyProcessPair {

    public RechargeWechatPaymentNotifyPair(HttpServletRequest request) {
        setBusinessSign(LARY.BUSINESS.PAYMENT.RECHARGE);
        setPaymentPlugin(LARY.PAYMENT.PLUGIN.WECHAT);
        setRequest(request);
    }
}
