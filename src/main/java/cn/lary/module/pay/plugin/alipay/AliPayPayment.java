package cn.lary.module.pay.plugin.alipay;

import cn.lary.module.common.CS.Lary;
import cn.lary.module.pay.dto.PayBuildDTO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.plugin.Payment;
import cn.lary.module.pay.service.PaymentLogService;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AliPayPayment implements Payment {

    private final PaymentLogService paymentLogService;
    private final AlipayClient alipayClient;

    @Override
    public PayBuildDTO pcPay(PayParam payParam) {

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setSubject(payParam.getSubject());
        model.setOutTradeNo(payParam.getSn());
        model.setTimeoutExpress("10m");
        model.setTotalAmount(payParam.getPrice().toString());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizModel(model);
        String notifyUrl = notifyUrl("v1","pc");
        String returnUrl = callBackUrl("v1","pc");
        alipayRequest.setNotifyUrl(notifyUrl);
        alipayRequest.setReturnUrl(returnUrl);

        // build post json
        JSONObject json = new JSONObject();
        json.put("rechargeId", payParam.getSn());
        json.put("amount", payParam.getPrice().toString());
        json.put("subject", payParam.getSubject());
        json.put("product_code", "FAST_INSTANT_TRADE_PAY");
        json.put("return_url",returnUrl);
        json.put("notify_url", notifyUrl);
        String postJson = json.toJSONString();
        try {
            AlipayTradePagePayResponse res = alipayClient.pageExecute(alipayRequest);
            String form = res.getBody();
            PayBuildDTO buildRes = new PayBuildDTO().setOk(true).setOutTradeNo(res.getOutTradeNo());
            buildRes.setContentType("text/html;charset=utf-8").setBody(form);
            // update payment log
            paymentLogService.update(new LambdaUpdateWrapper<PaymentLog>().set(PaymentLog::getPostStatus, Lary.PostStatus.success).set(PaymentLog::getPostJson,postJson)
                    .eq(PaymentLog::getOrderId, payParam.getSn()));
            return buildRes;
        } catch (AlipayApiException e) {
            log.error("alipay pay error{}",e.getMessage());
            paymentLogService.update(new LambdaUpdateWrapper<PaymentLog>().set(PaymentLog::getPostStatus, Lary.PostStatus.fail).set(PaymentLog::getPostJson,postJson)
                    .eq(PaymentLog::getOrderId, payParam.getSn()));
            return new PayBuildDTO().setOk(false).setErrMsg(e.getMessage()).setErrCode(e.getErrCode());
        }
    }

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
        return "alipay";
    }
}
