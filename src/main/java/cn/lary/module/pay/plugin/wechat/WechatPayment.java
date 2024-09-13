package cn.lary.module.pay.plugin.wechat;

import cn.lary.module.pay.plugin.Payment;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WechatPayment implements Payment {


    @Override
    public void callBack(HttpServletRequest request) {
        Payment.super.callBack(request);
    }

    @Override
    public void notify(HttpServletRequest request) {
        Payment.super.notify(request);
    }

    @Override
    public String getPluginName() {
        return "wechat";
    }
}
