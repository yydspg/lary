package cn.lary.payment.component.impl;

import cn.lary.common.constant.LARY;
import cn.lary.payment.component.PaymentNotifyProcessPair;
import cn.lary.payment.constant.PAYMENT;

import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
public class RechargeWechatPaymentNotifyPair extends PaymentNotifyProcessPair {

    public RechargeWechatPaymentNotifyPair(HttpServletRequest request) {
        setBusinessSign(LARY.BUSINESS.PAYMENT.RECHARGE);
        setPaymentPlugin(PAYMENT.PLUGIN.WECHAT);
        setRequest(request);
    }
}
