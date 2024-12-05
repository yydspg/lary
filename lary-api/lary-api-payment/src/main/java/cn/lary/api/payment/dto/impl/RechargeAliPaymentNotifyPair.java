package cn.lary.api.payment.dto.impl;


import cn.lary.api.payment.constant.PAYMENT;
import cn.lary.api.payment.dto.PaymentNotifyProcessPair;
import cn.lary.common.constant.LARY;


import lombok.Data;

import javax.servlet.http.HttpServletRequest;


@Data
public class RechargeAliPaymentNotifyPair extends PaymentNotifyProcessPair {

    public RechargeAliPaymentNotifyPair(HttpServletRequest request) {
        setBusinessSign(LARY.BUSINESS.PAYMENT.RECHARGE);
        setPaymentPlugin(PAYMENT.PLUGIN.ALI);
        setRequest(request);
    }
}
