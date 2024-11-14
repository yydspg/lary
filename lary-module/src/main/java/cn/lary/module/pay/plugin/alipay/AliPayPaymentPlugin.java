package cn.lary.module.pay.plugin.alipay;

import cn.lary.common.kit.JSONKit;
import cn.lary.common.kit.StringKit;
import cn.lary.module.common.constant.LARY;
import cn.lary.module.pay.component.PaymentQueryProcessPair;
import cn.lary.module.pay.plugin.AbstractPaymentPlugin;
import cn.lary.module.pay.component.BusinessPaymentNotifyManager;
import cn.lary.module.pay.component.PaymentNotifyProcessPair;
import cn.lary.module.pay.component.PaymentProcessPair;
import cn.lary.module.pay.dto.PaymentParamDTO;
import cn.lary.module.pay.entity.PaymentLog;
import cn.lary.module.pay.service.PaymentLogService;
import cn.lary.module.pay.vo.PaymentBuildVO;
import cn.lary.module.pay.vo.PaymentQueryVO;
import com.alibaba.fastjson2.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

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
    public boolean whetherOrderStatusSuccess(PaymentQueryVO vo) {
        if (StringKit.same(vo.getStatus(),"TRADE_SUCCESS")
        || StringKit.same(vo.getStatus(),"TRADE_FINISHED") ){
            return true;
        }
        return false;
    }

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

    @Override
    public  boolean check(Map<String, String> args) {
        String outTradeNo = args.get("out_trade_no");
        String totalAmount = args.get("total_amount");
        String appId = args.get("app_id");
        if (StringKit.diff(alipayConfig.getAppId(),appId)) {
            return false;
        }
        PaymentLog log = paymentLogService.lambdaQuery()
                .select(PaymentLog::getId)
                .eq(PaymentLog::getId, outTradeNo)
                .one();
        if (log == null || log.getPayStatus() == LARY.PAYMENT.STATUS.COMMIT) {
            return false;
        }
        return !StringKit.diff(totalAmount, log.getAmount().toString());
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
        log.error("alipay payment verify sign FAIL,params:{}",JSONKit.toJSON(pair.getParams()));
    }

    @Override
    protected void whenConvertParamsFail(PaymentNotifyProcessPair pair) {
        log.error("alipay convert params FAIL,params:{}", JSONKit.toJSON(pair.getParams()));
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
                    .setId(pair.getParam().getPayId())
                    .setAmount(pair.getParam().getAmount())
                    .setPayWay(pair.getPaymentWay())
                    .setPostJson(postJson)
                    .setPayStatus(LARY.PAYMENT.STATUS.INIT));
    }

    @Override
    protected void processAfterPaymentWhenSuccess(PaymentProcessPair pair) {
            paymentLogService.lambdaUpdate()
                    .set(PaymentLog::getPayStatus, LARY.PAYMENT.STATUS.COMMIT)
                    .eq(PaymentLog::getId, pair.getParam().getPayId());
    }

    @Override
    protected void processAfterPaymentWhenFail(PaymentProcessPair pair) {
            paymentLogService.lambdaUpdate()
                    .set(PaymentLog::getPayStatus, LARY.PAYMENT.STATUS.FAIL)
                    .eq(PaymentLog::getId, pair.getParam().getPayId());
    }

    @Override
    protected PaymentQueryVO processActiveQuery(PaymentQueryProcessPair pair) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(String.valueOf(pair.getPaymentId()));

        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            log.error("alipay payment query error,message:{}", e.getMessage());
            return new PaymentQueryVO()
                    .setPaymentId(pair.getPaymentId())
                    .setExecuteFail(true)
                    .setReason("alipay payment query error");
        }
        return new PaymentQueryVO()
                .setPaymentId(pair.getPaymentId())
                .setSn(response.getOutTradeNo())
                .setStatus(response.getTradeStatus())
                .setExecuteFail(false);
    }

    @Override
    protected void processQueryWhenFail(PaymentQueryVO pair) {
        businessPaymentNotifyManager.processQueryFail(pair);
    }

    @Override
    protected void processQueryWhenSuccess(PaymentQueryVO pair) {
        businessPaymentNotifyManager.processQuerySuccess(pair);
    }

    @Override
    protected void processQueryExecuteFail(PaymentQueryVO pair) {
        log.error("payment query execute FAIL,params:{}", JSONKit.toJSON(pair));
    }


    @Override
    public int getPaymentPlugin() {
        return LARY.PAYMENT.PLUGIN.ALI;
    }
}
