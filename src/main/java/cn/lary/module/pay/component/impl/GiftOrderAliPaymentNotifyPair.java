package cn.lary.module.pay.component.impl;

import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;


@Data
public class GiftOrderAliPaymentNotifyPair extends PaymentNotifyProcessPair {

    public GiftOrderAliPaymentNotifyPair(HttpServletRequest request) {
        setBusinessSign(LARY.BUSINESS.PAYMENT.GIFT);
        setPaymentPlugin(LARY.PAYMENT.PLUGIN.ALI);
        setRequest(request);
    }
}
