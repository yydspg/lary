package cn.lary.payment.api;

import cn.lary.payment.component.impl.GiftOrderAliPaymentNotifyPair;
import cn.lary.payment.component.impl.GiftOrderWechatPaymentNotifyPair;
import cn.lary.payment.component.impl.RechargeWechatPaymentNotifyPair;
import cn.lary.payment.plugin.PaymentPluginManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController("/v1/notify")
@RequiredArgsConstructor
public class PayNotifyController {

    private final PaymentPluginManager support;

    /**
     * 礼物模块的 alipay 支付后回调接口
     */
    @GetMapping("/gift/alipay")
    public void giftOrderAlipayNotify(HttpServletRequest request) {
        support.doNotify(new GiftOrderWechatPaymentNotifyPair(request));
    }
    /**
     * 礼物模块的 wechat 支付后回调接口*/
    @GetMapping("/gift/wechat")
    public void giftOrderWechatNotify(HttpServletRequest request) {
        support.doNotify(new GiftOrderWechatPaymentNotifyPair(request));
    }
    /**
     * 充值模块的 alipay 支付后回调接口
     */
    @GetMapping("/recharge/alipay")
    public void rechargeAlipayNotify(HttpServletRequest request) {
        support.doNotify(new GiftOrderAliPaymentNotifyPair(request));
    }
    /**
     * 充值模块的 wechat 支付后回调接口
     */
    @GetMapping("/recharge/wechat")
    public void rechargeWechatNotify(HttpServletRequest request) {
        support.doNotify(new RechargeWechatPaymentNotifyPair(request));
    }

}
