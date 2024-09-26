package cn.lary.module.pay.plugin.alipay;

import cn.lary.core.exception.SysException;
import cn.lary.module.common.CS.Lary;
import cn.lary.module.pay.vo.PayBuildVO;
import cn.lary.module.pay.dto.PayParam;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.plugin.Payment;
import cn.lary.module.pay.service.PaymentLogService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class AliPayPayment implements Payment {

    private final PaymentLogService paymentLogService;
    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;

    @Override
    public PayBuildVO pcPay(PayParam payParam) {

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setSubject(payParam.getSubject());
        model.setOutTradeNo(payParam.getPayId().toString());
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
        json.put("payId", payParam.getPayId());
        json.put("amount", payParam.getPrice().toString());
        json.put("subject", payParam.getSubject());
        json.put("product_code", "FAST_INSTANT_TRADE_PAY");
        json.put("return_url",returnUrl);
        json.put("notify_url", notifyUrl);
        String postJson = json.toJSONString();
        try {
            AlipayTradePagePayResponse res = alipayClient.pageExecute(alipayRequest);
            String form = res.getBody();
            PayBuildVO vo = new PayBuildVO()
                    .setOk(true)
                    .setOutTradeNo(res.getOutTradeNo());
            vo.setContentType("text/html;charset=utf-8").setBody(form);
            // update payment log
            paymentLogService.update(new LambdaUpdateWrapper<PaymentLog>()
                    .set(PaymentLog::getPostStatus, Lary.PostStatus.success)
                    .set(PaymentLog::getPostJson,postJson)
                    .eq(PaymentLog::getPayId, payParam.getPayId()));
            return vo;
        } catch (AlipayApiException e) {
            log.error("alipay pay error{}",e.getMessage());
            paymentLogService.update(new LambdaUpdateWrapper<PaymentLog>()
                    .set(PaymentLog::getPostStatus, Lary.PostStatus.fail)
                    .set(PaymentLog::getPostJson,postJson)
                    .eq(PaymentLog::getPayId, payParam.getPayId()));
            return new PayBuildVO()
                    .setOk(false)
                    .setErrMsg(e.getMessage())
                    .setErrCode(e.getErrCode());
        }
    }

    @Override
    public void callBack(HttpServletRequest request,int biz) {
        Map<String, String> params = convertRequestParamsToMap(request); // 将异步通知中收到的待验证所有参数都存放到map中
        String paramsJson = JSON.toJSONString(params);
        log.info("支付宝回调，{}", paramsJson);
        try {
            boolean signVerified = AlipaySignature.rsaCheckV2(params, alipayConfig.getAlipayPublicKey(),
                    alipayConfig.getCharset(), alipayConfig.getSignType());
            // biz execute
            if (signVerified) {
                // ok

            }else{

            }
        } catch (AlipayApiException e) {
            throw new SysException("alipay pay error", e);
        }
    }

    @Override
    public void notify(HttpServletRequest request) {

    }

    @Override
    public String getPluginName() {
        return "alipay";
    }

    @Override
    public Integer getPayWay() {
        return Lary.PayWay.alipay;
    }

    // 将request中的参数转换成Map
    private static Map<String, String> convertRequestParamsToMap(HttpServletRequest request) {
        Map<String, String> retMap = new HashMap<String, String>();
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            int valLen = values.length;
            if (valLen == 1) {
                retMap.put(name, values[0]);
            } else if (valLen > 1) {
                StringBuilder sb = new StringBuilder();
                for (String val : values) {
                    sb.append(",").append(val);
                }
                retMap.put(name, sb.substring(1));
            } else {
                retMap.put(name, "");
            }
        }
        return retMap;
    }

}
