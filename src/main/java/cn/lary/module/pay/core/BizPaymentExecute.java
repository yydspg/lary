package cn.lary.module.pay.core;

import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.plugin.PluginSupport;
import cn.lary.module.pay.service.PaymentLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public  class BizPaymentExecute {
    private final PluginSupport pluginSupport;
    private final PaymentLogService paymentLogService;

    protected void pay(PayParam payParam, HttpServletRequest request, HttpServletResponse response){
        PaymentLog log = new PaymentLog();
    }
    protected  void  notify(HttpServletRequest request){

    };
    protected  void callback(HttpServletRequest request) {

    };

}
