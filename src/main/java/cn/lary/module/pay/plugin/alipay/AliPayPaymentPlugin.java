package cn.lary.module.pay.plugin.alipay;

import cn.lary.kit.JSONKit;
import cn.lary.kit.StringKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.AbstractPaymentPlugin;
import cn.lary.module.pay.component.BusinessPaymentNotifyManager;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.pay.dto.PaymentParamDTO;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.service.PaymentLogService;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
public class AliPayPaymentPlugin extends AbstractPaymentPlugin {

    private final PaymentLogService paymentLogService;
    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;
    private final BusinessPaymentNotifyManager businessPaymentNotifyManager;
    private final String NOTIFY_URL = "https://openapi.alipay.com/gateway.do";
    
    @Override
    public PaymentBuildVO pcPay(PaymentParamDTO dto) {

        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setSubject(dto.getSubject());
        model.setOutTradeNo(String.valueOf(dto.getPayId()));
        model.setTimeoutExpress("10m");
        model.setTotalAmount(String.valueOf(dto.getAmount()));
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        alipayRequest.setBizModel(model);
        alipayRequest.setNotifyUrl(NOTIFY_URL);
        alipayRequest.setReturnUrl("test");
        
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
            String form = response.getBody();
            PaymentBuildVO vo = new PaymentBuildVO()
                    .setFail(false)
                    .setOutTradeNo(response.getOutTradeNo());
            vo.setContentType("text/html;charset=utf-8").setBody(form);
            return vo;
        } catch (AlipayApiException e) {
            log.error("alipay pay error,message:{},paymentId:{}",e.getMessage(),dto.getPayId());
            return new PaymentBuildVO()
                    .setFail(true)
                    .setErrMsg(e.getMessage())
                    .setErrCode(e.getErrCode());
        }
    }


    private PaymentLog check(Map<String, String> args) {
        String outTradeNo = args.get("out_trade_no");
        String totalAmount = args.get("total_amount");
        String sellId = args.get("sell_id");
        String appId = args.get("app_id");
        if (StringKit.diff(alipayConfig.getAppId(),appId)) {
            return null;
        }
        PaymentLog log = paymentLogService.getOne(new LambdaQueryWrapper<PaymentLog>().eq(PaymentLog::getPayId, outTradeNo));
        if (log == null || log.getPayStatus() == LARY.PayStatus.fail) {
            return null;
        }
        if(StringKit.diff(totalAmount,log.getPayCost().toString())) {
            return null;
        }
        return log;
    }


    @Override
    protected Map<String, String> getParamsFromRequest(HttpServletRequest request) throws Exception {
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

    @Override
    protected boolean verificationSign(Map<String, String> params) throws Exception {
         return AlipaySignature.rsaCheckV2(params, alipayConfig.getAlipayPublicKey(),
                alipayConfig.getCharset(), alipayConfig.getSignType());
    }

    @Override
    protected void whenVerificationSignFail(PaymentNotifyProcessPair pair) {
        log.error("alipay payment verify sign fail,params:{}",JSONKit.toJSON(pair.getParams()));
    }

    @Override
    protected void whenConvertParamsFail(PaymentNotifyProcessPair pair) {
        log.error("alipay convert params fail,params:{}", JSONKit.toJSON(pair.getParams()));
    }

    @Override
    protected void processNotifyWhenSuccess(PaymentNotifyProcessPair pair) {
        businessPaymentNotifyManager.processFail(pair);
    }

    @Override
    protected void processNotifyWhenFailure(PaymentNotifyProcessPair pair) {
        businessPaymentNotifyManager.processSuccess(pair);
    }

    @Override
    protected void processBeforePayment(PaymentProcessPair pair) {
        JSONObject json = new JSONObject();
        json.put("payId", pair.getParam().getPayId());
        json.put("amount", pair.getParam().getAmount());
        json.put("subject", pair.getParam().getSubject());
        json.put("product_code", "FAST_INSTANT_TRADE_PAY");
        json.put("return_url","test");
        json.put("notify_url", NOTIFY_URL);
        String postJson = json.toJSONString();
            paymentLogService.save(new PaymentLog()
                    .setPayId(pair.getParam().getPayId())
                    .setPayCost(pair.getParam().getAmount())
                    .setPayWay(pair.getPaymentWay())
                    .setPostJson(postJson)
                    .setPayStatus(LARY.PAYMENT.STATUS.INIT));
    }

    @Override
    protected void processAfterPaymentWhenSuccess(PaymentProcessPair pair) {
            paymentLogService.lambdaUpdate()
                    .set(PaymentLog::getPayStatus, LARY.PAYMENT.STATUS.COMMIT)
                    .eq(PaymentLog::getPayId, pair.getParam().getPayId());
    }

    @Override
    protected void processAfterPaymentWhenFail(PaymentProcessPair pair) {
            paymentLogService.lambdaUpdate()
                    .set(PaymentLog::getPayStatus, LARY.PAYMENT.STATUS.FAIL)
                    .eq(PaymentLog::getPayId, pair.getParam().getPayId());
    }


    @Override
    public int getPaymentPlugin() {
        return LARY.PAYMENT.PLUGIN.ALI;
    }
}
