package cn.lary.module.pay.api;

import cn.lary.module.common.constant.Lary;
import cn.lary.module.pay.plugin.PluginSupport;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/v1/callback")
@RequiredArgsConstructor
public class PayCallbackController {

    private final PluginSupport support;

    /**
     * 礼物模块的 alipay 支付后回调接口
     * @param req {@link HttpServletRequest}
     */
    @GetMapping("/gift/alipay")
    public void giftOrderAlipayCallback(HttpServletRequest req) {
        support.callback(req, Lary.PayWay.alipay,Lary.PayBiz.gift);
    }
    /**
     * 礼物模块的 wechat 支付后回调接口
     * @param req {@link HttpServletRequest}
     */
    @GetMapping("/gift/wechat")
    public void giftOrderWechatCallback(HttpServletRequest req) {
        support.callback(req, Lary.PayWay.wechat,Lary.PayBiz.gift);
    }
    /**
     * 充值模块的 alipay 支付后回调接口
     * @param req {@link HttpServletRequest}
     */
    @GetMapping("/recharge/alipay")
    public void rechargeCallback(HttpServletRequest req) {
        support.callback(req,Lary.PayWay.alipay,Lary.PayBiz.recharge);
    }
    /**
     * 充值模块的 wechat 支付后回调接口
     * @param req {@link HttpServletRequest}
     */
    @GetMapping("/recharge/wechat")
    public void rechargeWechatCallback(HttpServletRequest req) {
        support.callback(req,Lary.PayWay.wechat,Lary.PayBiz.recharge);
    }

}
