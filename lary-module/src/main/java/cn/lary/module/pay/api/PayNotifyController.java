package cn.lary.module.pay.api;

import cn.lary.module.pay.component.impl.GiftOrderAliPaymentNotifyPair;
import cn.lary.module.pay.component.impl.GiftOrderWechatPaymentNotifyPair;
import cn.lary.module.pay.component.impl.RechargeWechatPaymentNotifyPair;
import cn.lary.module.pay.plugin.PaymentPluginManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/v1/callback")
@RequiredArgsConstructor
public class PayNotifyController {

    private final PaymentPluginManager support;

    /**
     * 礼物模块的 alipay 支付后回调接口
     */
    @GetMapping("/gift/alipay")
    public void giftOrderAlipayNotify(HttpServletRequest request) {
        support.doCallback(new GiftOrderWechatPaymentNotifyPair(request));
    }
    /**
     * 礼物模块的 wechat 支付后回调接口*/
    @GetMapping("/gift/wechat")
    public void giftOrderWechatNotify(HttpServletRequest request) {
        support.doCallback(new GiftOrderWechatPaymentNotifyPair(request));
    }
    /**
     * 充值模块的 alipay 支付后回调接口
     */
    @GetMapping("/recharge/alipay")
    public void rechargeAlipayNotify(HttpServletRequest request) {
        support.doCallback(new GiftOrderAliPaymentNotifyPair(request));
    }
    /**
     * 充值模块的 wechat 支付后回调接口
     */
    @GetMapping("/recharge/wechat")
    public void rechargeWechatNotify(HttpServletRequest request) {
        support.doCallback(new RechargeWechatPaymentNotifyPair(request));
    }

}
